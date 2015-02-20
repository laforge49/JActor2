package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.messages.AIOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class EndStage extends IsolationBladeBase implements DataProcessor {

    public EndStage() throws Exception {
    }

    @Override
    public AIOp<Void> processDataAOp(final FirehoseData _firehoseData) {
        return new AIOp<Void>("endStage", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
