package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Transport;

public class AtomicTest extends TestCase {
    int count = 0;

    public void test() throws Exception {
        JAContext jaContext = new JAContext();
        try {
            int _count = startReq1(new AtomicMailbox(jaContext)).call();
            assertEquals(5, _count);
        } finally {
            jaContext.close();
        }
    }

    Request<Integer> startReq1(final Mailbox _mailbox) {
        return new Request<Integer>(_mailbox) {
            @Override
            public void processRequest(final Transport<Integer> _rp)
                    throws Exception {
                Mailbox mailbox = new AtomicMailbox(_mailbox.getJAContext());
                ResponseProcessor rc = new ResponseCounter(5, null,
                        new ResponseProcessor() {
                            @Override
                            public void processResponse(Object response)
                                    throws Exception {
                                _rp.processResponse(count);
                            }
                        });
                aReq(mailbox, 1).send(_mailbox, rc);
                aReq(mailbox, 2).send(_mailbox, rc);
                aReq(mailbox, 3).send(_mailbox, rc);
                aReq(mailbox, 4).send(_mailbox, rc);
                aReq(mailbox, 5).send(_mailbox, rc);
            }
        };
    }

    Request<Void> aReq(final Mailbox _mailbox, final int msg) {
        return new Request<Void>(_mailbox) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                Delay delay = new Delay(_mailbox.getJAContext());
                delay.sleepReq(100 - (msg * 20)).send(_mailbox,
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
}
