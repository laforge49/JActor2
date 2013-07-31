package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.core.Transport;
import org.agilewiki.jactor2.util.UtilMailboxFactory;

public class EndStage extends ActorBase implements DataProcessor {

    public EndStage(final UtilMailboxFactory _mailboxFactory)
            throws Exception {
        initialize(_mailboxFactory.createAtomicMailbox());
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processResponse(null);
                _transport.processResponse(null);
            }
        };
    }
}
