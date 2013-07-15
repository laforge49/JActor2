package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.api.ActorBase;
import org.agilewiki.jactor2.api.BoundRequest;
import org.agilewiki.jactor2.api.BoundRequestBase;
import org.agilewiki.jactor2.api.Transport;
import org.agilewiki.jactor2.util.UtilMailboxFactory;

public class EndStage extends ActorBase implements DataProcessor {

    public EndStage(final UtilMailboxFactory _mailboxFactory)
            throws Exception {
        initialize(_mailboxFactory.createMayBlockMailbox());
    }

    @Override
    public BoundRequest<Void> processDataReq(final FirehoseData _firehoseData) {
        return new BoundRequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processResponse(getMailbox(), null);
                _transport.processResponse(null);
            }
        };
    }
}
