package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

import java.util.List;

public class NullStage extends ActorBase implements DataProcessor {

    private DataProcessor next;

    public long total;

    public NullStage(final JAContext _jaContext, final DataProcessor _next)
            throws Exception {
        next = _next;
        initialize(new AtomicMailbox(_jaContext));
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new Request<Void>(getMailbox()) {
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
                next.processDataReq(_firehoseData).signal();
                _transport.processResponse(null);
            }
        };
    }
}
