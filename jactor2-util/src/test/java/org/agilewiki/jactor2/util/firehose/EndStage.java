package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;

public class EndStage extends ActorBase implements DataProcessor {

    public EndStage(final JAContext _jaContext)
            throws Exception {
        initialize(new AtomicMessageProcessor(_jaContext));
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processResponse(null);
                _transport.processResponse(null);
            }
        };
    }
}
