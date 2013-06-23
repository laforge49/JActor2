package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.*;

public class NullStage extends ActorBase implements DataProcessor {

    private DataProcessor next;

    public NullStage(final Mailbox _mailbox, final DataProcessor _next)
            throws Exception {
        next = _next;
        initialize(_mailbox);
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                next.processDataReq(_firehoseData).signal(getMailbox());
                _transport.processResponse(null);
            }
        };
    }
}
