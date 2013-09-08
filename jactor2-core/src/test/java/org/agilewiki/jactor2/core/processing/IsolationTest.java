package org.agilewiki.jactor2.core.processing;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Delay;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ResponseCounter;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class IsolationTest extends TestCase {
    int count = 0;

    public void test() throws Exception {
        ModuleContext moduleContext = new ModuleContext();
        try {
            int _count = startReq1(new IsolationMessageProcessor(moduleContext)).call();
            assertEquals(5, _count);
        } finally {
            moduleContext.close();
        }
    }

    AsyncRequest<Integer> startReq1(final MessageProcessor _messageProcessor) {
        return new AsyncRequest<Integer>(_messageProcessor) {
            AsyncRequest<Integer> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                MessageProcessor messageProcessor = new IsolationMessageProcessor(_messageProcessor.getModuleContext());
                AsyncResponseProcessor rc = new ResponseCounter(5, null,
                        new AsyncResponseProcessor() {
                            @Override
                            public void processAsyncResponse(Object response)
                                    throws Exception {
                                dis.processAsyncResponse(count);
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

    AsyncRequest<Void> aReq(final MessageProcessor _messageProcessor, final int msg) {
        return new AsyncRequest<Void>(_messageProcessor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                Delay delay = new Delay(_messageProcessor.getModuleContext());
                delay.sleepReq(100 - (msg * 20)).send(_messageProcessor,
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(Void response)
                                    throws Exception {
                                if (count != msg - 1)
                                    throw new IllegalStateException();
                                count = msg;
                                dis.processAsyncResponse(null);
                            }
                        });
            }
        };
    }
}
