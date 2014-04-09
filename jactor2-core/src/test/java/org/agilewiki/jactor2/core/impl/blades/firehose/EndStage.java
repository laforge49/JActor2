package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class EndStage extends IsolationBladeBase implements DataProcessor {

    public EndStage() throws Exception {
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                processAsyncResponse(null);
            }
        };
    }
}
