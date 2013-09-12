package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.BladeBase;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class EndStage extends BladeBase implements DataProcessor {

    public EndStage(final Facility _facility)
            throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    @Override
    public AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData) {
        return new AsyncRequest<Void>(getReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                processAsyncResponse(null);
            }
        };
    }
}
