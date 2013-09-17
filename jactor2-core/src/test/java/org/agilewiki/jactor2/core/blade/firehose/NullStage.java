package org.agilewiki.jactor2.core.blade.firehose;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.util.List;

public class NullStage extends BladeBase implements DataProcessor {

    private DataProcessor next;

    public long total;

    public NullStage(final Facility _facility, final DataProcessor _next)
            throws Exception {
        next = _next;
        initialize(new IsolationReactor(_facility));
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                //Thread.sleep(1);
                List<Long> list = _firehoseData.getContent();
                int s = list.size();
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
