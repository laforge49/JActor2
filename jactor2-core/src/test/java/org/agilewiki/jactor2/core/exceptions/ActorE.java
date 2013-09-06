package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorE {
    private final MessageProcessor messageProcessor;

    public ActorE(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);
    }

    public Request<Void> throwRequest(final ActorA actorA) {
        return new Request<Void>(messageProcessor) {
            Request<Void> dis = this;

            @Override
            public void processRequest()
                    throws Exception {
                // Note: we only respond to responseProcessor if we get a
                // response to our own request, which should NOT happen.
                // Therefore, responseProcessor is NOT called.
                try {
                    actorA.throwRequest.send(messageProcessor,
                            new ResponseProcessor<Void>() {

                                @Override
                                public void processResponse(final Void response)
                                        throws Exception {
                                    // Should NOT happen!
                                    dis.processException(new IllegalStateException(
                                            "We should have never got here!"));
                                }
                            });
                } catch (final Exception e) {
                    // Make sure we also don't throw anything, which would be
                    // passed to responseProcessor as a response ...
                    e.printStackTrace();
                }
            }
        };
    }
}
