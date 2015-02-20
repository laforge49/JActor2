package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.AIOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

import java.util.List;

public class NullStage extends IsolationBladeBase implements DataProcessor {

    private final DataProcessor next;

    public long total;

    public NullStage(final DataProcessor _next) throws Exception {
        next = _next;
    }

    @Override
    public AIOp<Void> processDataAOp(final FirehoseData _firehoseData) {
        return new AIOp<Void>("nullStage", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                final List<Long> list = _firehoseData.getContent();
                final int s = list.size();
                total = 0;
                int x = 0;
                while (x < s) {
                    total += list.get(x);
                    x += 1;
                }
                next.processDataAOp(_firehoseData).signal();
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
