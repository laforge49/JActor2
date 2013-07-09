package org.agilewiki.jactor2.util.atomic;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.*;
import org.agilewiki.jactor2.util.Delay;
import org.agilewiki.jactor2.util.ResponseCounter;
import org.agilewiki.jactor2.util.UtilMailboxFactory;

public class AtomicTest extends TestCase {
    int count = 0;

    public void test1() throws Exception {
        UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        try {
            int _count = startReq1(mailboxFactory.createNonBlockingMailbox()).call();
            assertEquals(5, _count);
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Integer> startReq1(final Mailbox _mailbox) {
        return new RequestBase<Integer>(_mailbox) {
            @Override
            public void processRequest(final Transport<Integer> _rp)
                    throws Exception {
                final FifoRequestProcessor ap = FifoRequestProcessor
                        .create(getMailbox().getMailboxFactory());
                ResponseProcessor rc = new ResponseCounter(5, null,
                        new ResponseProcessor() {
                            @Override
                            public void processResponse(Object response)
                                    throws Exception {
                                _rp.processResponse(count);
                            }
                        });
                ap.atomicReq(aReq(ap, 1)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 2)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 3)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 4)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 5)).send(_mailbox, rc);
            }
        };
    }

    Request<Void> aReq(final FifoRequestProcessor ap, final int msg) {
        final Mailbox mailbox = ap.getMailbox();
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                Delay delay = new Delay(mailbox.getMailboxFactory());
                delay.sleepReq(100 - (msg * 20)).send(mailbox,
                        new ResponseProcessor<Void>() {
                            @Override
                            public void processResponse(Void response)
                                    throws Exception {
                                if (count != msg - 1)
                                    throw new IllegalStateException();
                                count = msg;
                                _rp.processResponse(null);
                            }
                        });
            }
        };
    }

    public void test2() throws Exception {
        UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        try {
            final FifoRequestProcessor fp = new FifoRequestProcessor();
            fp.initialize(mailboxFactory.createMayBlockMailbox(fp));
            fp.atomicReq(bReq(fp.getMailbox())).call();
        } catch (UnsupportedOperationException uoe) {
            mailboxFactory.close();
            return;
        }
        throw new IllegalStateException();
    }

    Request<Void> bReq(final Mailbox _mailbox) {
        return new RequestBase<Void>(_mailbox) {
            @Override
            public void processRequest(Transport<Void> responseProcessor)
                    throws Exception {
                throw new UnsupportedOperationException("it happen");
            }
        };
    }
}
