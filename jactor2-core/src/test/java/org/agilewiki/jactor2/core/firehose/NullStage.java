package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

import java.util.List;

public class NullStage extends ActorBase implements DataProcessor {

    private DataProcessor next;

    public long total;

    public NullStage(final ModuleContext _moduleContext, final DataProcessor _next)
            throws Exception {
        next = _next;
        initialize(new IsolationMessageProcessor(_moduleContext));
    }

    @Override
    public Request<Void> processDataReq(final FirehoseData _firehoseData) {
        return new Request<Void>(getMessageProcessor()) {
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
                next.processDataReq(_firehoseData).send(getMessageProcessor(), null);
                _transport.processResponse(null);
            }
        };
    }
}
