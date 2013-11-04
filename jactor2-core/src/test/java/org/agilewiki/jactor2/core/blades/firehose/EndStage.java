package org.agilewiki.jactor2.core.blades.firehose;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class EndStage extends BladeBase implements DataProcessor {

    public EndStage(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                processAsyncResponse(null);
            }
        };
    }
}
