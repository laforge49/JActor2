package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class EndStage extends ActorBase implements DataProcessor {

    public EndStage(final ModuleContext _moduleContext)
            throws Exception {
        initialize(new IsolationMessageProcessor(_moduleContext));
    }

    @Override
    public AsyncRequest<Void> processDataReq(final FirehoseData _firehoseData) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                Thread.sleep(1);
                _firehoseData.getAck().processAsyncResponse(null);
                processAsyncResponse(null);
            }
        };
    }
}
