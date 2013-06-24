package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.util.BoundResponseProcessor;
import org.agilewiki.jactor.util.UtilMailboxFactory;

import java.util.ArrayList;
import java.util.List;

public class FirstStage extends ActorBase implements Runnable {

    private DataProcessor next;

    private long count;

    private long ndx;

    private int maxWindowSize;

    private int ackCount;

    private List<Long> list;

    private FirehoseData firehoseData;

    private BoundResponseProcessor<Void> ack;

    Thread mainThread;

    public FirstStage(final UtilMailboxFactory _mailboxFactory,
                      final DataProcessor _next,
                      final long _count,
                      final int _maxWindowSize)
            throws Exception {
        System.out.println("create");//todo
        mainThread = Thread.currentThread();
        next = _next;
        count = _count;
        maxWindowSize = _maxWindowSize;
        initialize(_mailboxFactory.createMailbox(true, this));
        ack = new BoundResponseProcessor<Void>(getMailbox(), new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                System.out.println("got ack, ackCount = "+ackCount);//todo
                ackCount -= 1;
                if (list != null) {
                    send();
                }
                if (ackCount == 0 && ndx >= _count)
                    mainThread.interrupt();
            }
        });
        System.out.println("created");//todo
        new RequestBase<Void>(getMailbox()) {

            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
            }
        }.signal();
    }

    private void createList() {
        if (list != null)
            return;
        list = new ArrayList<Long>();
        firehoseData = new FirehoseData(ack, list);
    }

    private void send() throws Exception {
        next.processDataReq(firehoseData).signal(getMailbox());
        System.out.println("sent "+list.size());//todo
        list = null;
        firehoseData = null;
        ackCount += 1;
    }

    private void exception(Exception e) {
        e.printStackTrace();
        try {
            getMailbox().getMailboxFactory().close();
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
    }

    private void add() {
        ndx += 1;
        list.add(ndx);
    }

    @Override
    public void run() {
        System.out.println("run");
        while (ndx < count && ackCount < maxWindowSize) {
            createList();
            add();
            try {
                send();
                getMailbox().flush();
            } catch (Exception e) {
                exception(e);
            }
        }
        if (ndx >= count)
            return;
        createList();
        while (getMailbox().isEmpty() && ndx < count) {
            add();
        }
    }
}
