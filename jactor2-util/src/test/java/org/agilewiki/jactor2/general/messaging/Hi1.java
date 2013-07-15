package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.RequestBase;
import org.agilewiki.jactor2.api.Transport;

public class Hi1 extends RequestBase<String, Actor1> {
    @Override
    public void processRequest(
            final Actor1 actor1,
            final Transport<String> responseProcessor)
            throws Exception {
        responseProcessor.processResponse(actor1.hi());
    }
}
