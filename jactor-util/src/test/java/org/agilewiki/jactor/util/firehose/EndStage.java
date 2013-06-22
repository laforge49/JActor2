package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.ActorBase;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;

public class EndStage extends ActorBase implements DataProcessor {
    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processResponse(getMailbox(), null);
            }
        };
    }
}
