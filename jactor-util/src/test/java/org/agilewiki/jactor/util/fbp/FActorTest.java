package org.agilewiki.jactor.util.fbp;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

public class FActorTest extends TestCase {
    public void test1() throws Exception {
        int count = 100000;
        MailboxFactory testMBF = new DefaultMailboxFactoryImpl();
        try {
            final SrcFActor srcFActor = new SrcFActor(count, testMBF);
            final TrgtFActor trgtFActor = new TrgtFActor(count, testMBF, Thread.currentThread());
            new Flow(10000, srcFActor, trgtFActor);
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
            System.out.println("per second = " + (count * 1000 / d));
        } finally {
            testMBF.close();
        }
    }
}

class SrcFActor extends FActor {

    int i;

    OutPort outPort;

    int count;

    public SrcFActor(final int _count, final MailboxFactory _mailboxFactory) {
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
        if (i < count) {
            i += 1;
            write(_outPort, i);
        } else
            activity = idle;

    }

    @Override
    public void addOutPort(final OutPort _outPort) {
        super.addOutPort(_outPort);
        outPort = _outPort;
    }
}

class TrgtFActor extends FActor {

    InPort inPort;

    Thread thread;

    int count;

    public TrgtFActor(final int _count, final MailboxFactory _mailboxFactory, final Thread _thread) {
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
        int i = (Integer) _e;
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
