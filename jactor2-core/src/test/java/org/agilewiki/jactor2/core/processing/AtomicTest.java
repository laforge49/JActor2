package org.agilewiki.jactor2.core.processing;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Delay;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseCounter;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Transport;

public class AtomicTest extends TestCase {
    int count = 0;

    public void test() throws Exception {
        JAContext jaContext = new JAContext();
        try {
            int _count = startReq1(new AtomicMessageProcessor(jaContext)).call();
            assertEquals(5, _count);
        } finally {
            jaContext.close();
        }
    }

    Request<Integer> startReq1(final MessageProcessor _messageProcessor) {
        return new Request<Integer>(_messageProcessor) {
            @Override
            public void processRequest(final Transport<Integer> _rp)
                    throws Exception {
                MessageProcessor messageProcessor = new AtomicMessageProcessor(_messageProcessor.getJAContext());
                ResponseProcessor rc = new ResponseCounter(5, null,
                        new ResponseProcessor() {
                            @Override
                            public void processResponse(Object response)
                                    throws Exception {
                                _rp.processResponse(count);
                            }
                        });
                aReq(messageProcessor, 1).send(_messageProcessor, rc);
                aReq(messageProcessor, 2).send(_messageProcessor, rc);
                aReq(messageProcessor, 3).send(_messageProcessor, rc);
                aReq(messageProcessor, 4).send(_messageProcessor, rc);
                aReq(messageProcessor, 5).send(_messageProcessor, rc);
            }
        };
    }

    Request<Void> aReq(final MessageProcessor _messageProcessor, final int msg) {
        return new Request<Void>(_messageProcessor) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                Delay delay = new Delay(_messageProcessor.getJAContext());
                delay.sleepReq(100 - (msg * 20)).send(_messageProcessor,
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
