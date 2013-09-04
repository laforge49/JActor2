package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

public class EndStage extends ActorBase implements DataProcessor {

    public EndStage(final ModuleContext _moduleContext)
            throws Exception {
        initialize(new IsolationMessageProcessor(_moduleContext));
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
