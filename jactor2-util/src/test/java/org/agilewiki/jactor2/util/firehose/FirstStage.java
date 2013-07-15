package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.api.ActorBase;
import org.agilewiki.jactor2.api.RequestBase;
import org.agilewiki.jactor2.api.ResponseProcessor;
import org.agilewiki.jactor2.api.Transport;
import org.agilewiki.jactor2.util.BoundResponseProcessor;
import org.agilewiki.jactor2.util.UtilMailboxFactory;

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

    long t0;

    public FirstStage(final UtilMailboxFactory _mailboxFactory,
                      final DataProcessor _next,
                      final long _count,
                      final int _maxWindowSize)
            throws Exception {
        mainThread = Thread.currentThread();
        next = _next;
        count = _count;
        maxWindowSize = _maxWindowSize;
        initialize(_mailboxFactory.createMayBlockMailbox(this));
        ack = new BoundResponseProcessor<Void>(getMailbox(), new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                ackCount -= 1;
                if (list != null) {
                    send();
                }
                if (ackCount == 0 && ndx >= _count) {
                    long t1 = System.currentTimeMillis();
                    long d = t1 - t0;
                    System.out.println("time in millis: " + d);
                    System.out.println("number of Long: " + count);
                    System.out.println("window size: " + maxWindowSize);
                    if (d > 0) {
                        System.out.println("Longs/second through the firehose: " + count * 1000L / d);
                        System.out.println("Longs/second passed between stages: " + 10L * count * 1000L / d);
                    }
                    mainThread.interrupt();
                }
            }
        });
        t0 = System.currentTimeMillis();
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
        next.processDataReq(firehoseData).signal();
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
