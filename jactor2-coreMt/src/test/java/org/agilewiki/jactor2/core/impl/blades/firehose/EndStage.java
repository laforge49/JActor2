package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class EndStage extends IsolationBladeBase implements DataProcessor {

    public EndStage() throws Exception {
    }

    @Override
    public AOp<Void> processDataAOp(final FirehoseData _firehoseData) {
        return new AOp<Void>("endStage", getReactor()) {
            @Override
            public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
