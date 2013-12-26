package org.agilewiki.jactor2.core.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.util.List;

public class NullStage extends IsolationBladeBase implements DataProcessor {

    private final DataProcessor next;

    public long total;

    public NullStage(final Plant _plant, final DataProcessor _next)
            throws Exception {
        initialize(new IsolationReactor());
        next = _next;
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                //Thread.sleep(1);
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
