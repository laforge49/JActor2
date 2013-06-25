package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.util.UtilMailboxFactory;

import java.util.List;

public class NullStage extends ActorBase implements DataProcessor {

    private DataProcessor next;

    public long total;

    public NullStage(final UtilMailboxFactory _mailboxFactory, final DataProcessor _next)
            throws Exception {
        next = _next;
        initialize(_mailboxFactory.createMailbox(true));
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                //Thread.sleep(1);
                List<Long> list = _firehoseData.getContent();
                int s = list.size();
                total = 0;
                int x = 0;
                while (x < s) {
                    total += list.get(x);
                    x += 1;
                }
                next.processDataReq(_firehoseData).signal(getMailbox());
                _transport.processResponse(null);
            }
        };
    }
}
