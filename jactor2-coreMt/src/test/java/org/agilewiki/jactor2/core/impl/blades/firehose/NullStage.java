package org.agilewiki.jactor2.core.impl.blades.firehose;

import java.util.List;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class NullStage extends IsolationBladeBase implements DataProcessor {

    private final DataProcessor next;

    public long total;

    public NullStage(final DataProcessor _next) throws Exception {
        next = _next;
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() {
                final List<Long> list = _firehoseData.getContent();
                final int s = list.size();
                total = 0;
                int x = 0;
                while (x < s) {
                    total += list.get(x);
                    x += 1;
                }
                send(next.processDataAReq(_firehoseData), null);
                processAsyncResponse(null);
            }
        };
    }
}
