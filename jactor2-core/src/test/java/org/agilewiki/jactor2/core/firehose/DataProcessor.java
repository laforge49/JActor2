package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.Blade;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;

public interface DataProcessor extends Blade {

    AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData);
}
