package org.agilewiki.jactor.util.fbp;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class FActorBTest extends TestCase {
    public void test1() throws Exception {
        int count = 100000000;
        MailboxFactory testMBF = new DefaultMailboxFactoryImpl();
        try {
            final SrcFActorB srcFActor = new SrcFActorB(count, testMBF);
            final TrgtFActorB trgtFActor = new TrgtFActorB(count, testMBF, Thread.currentThread());
            new Flow(100000, srcFActor, trgtFActor);
            long t0 = System.currentTimeMillis();
            new RequestBase<Void>(srcFActor.getMailbox()){
                @Override
                public void processRequest(Transport<Void> _transport) throws Exception {
                    srcFActor.wrote(srcFActor.outPort);
                }
            }.signal();
            new RequestBase<Void>(trgtFActor.getMailbox()){
                @Override
                public void processRequest(Transport<Void> _transport) throws Exception {
                    trgtFActor.activity = trgtFActor.inPort;
                }
            }.signal();
            try {
                Thread.sleep(1000*60*60);
            } catch (Exception ex) {
            }
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("per second = " + (count * 1000L / d));
        } finally {
            testMBF.close();
        }
    }
}

class SrcFActorB extends FActor {

    int i;

    OutPort outPort;

    int count;

    public SrcFActorB(final int _count, final MailboxFactory _mailboxFactory) {
        super(_mailboxFactory);
        count = _count;
    }

    @Override
    public void closed(final InPort _inPort) {
        close();
    }

    @Override
    public void closed(final OutPort _outPort) {
        close();
    }

    @Override
    public void gotNext(final InPort _inport, Object _e) {
    }

    @Override
    public void wrote(final OutPort _outPort) {
        int s = 10000;
        List<Integer> lst = new ArrayList<Integer>(s);
//        while (i < count && lst.size() < s && !_outPort.full()) {
        while (i < count && lst.size() < s) {
            i += 1;
            lst.add(i);
        }
        if (lst.size() > 0)
            write(_outPort, lst);
        else if (i >= count)
            activity = idle;
    }

    @Override
    public void addOutPort(final OutPort _outPort) {
        super.addOutPort(_outPort);
        outPort = _outPort;
    }
}

class TrgtFActorB extends FActor {

    InPort inPort;

    Thread thread;

    int count;

    public TrgtFActorB(final int _count, final MailboxFactory _mailboxFactory, final Thread _thread) {
        super(_mailboxFactory);
        count = _count;
        thread = _thread;
    }

    @Override
    public void closed(final InPort _inPort) {
        close();
    }

    @Override
    public void closed(final OutPort _outPort) {
        close();
    }

    @Override
    public void gotNext(final InPort _inport, final Object _e) {
        List<Integer> lst = (List<Integer>) _e;
        int i = lst.get(lst.size() - 1);
        if (i == count) {
            thread.interrupt();
        }
    }

    @Override
    public void wrote(final OutPort _outPort) {
    }

    @Override
    public void addInPort(final InPort _inPort) {
        super.addInPort(_inPort);
        inPort = _inPort;
    }
}
