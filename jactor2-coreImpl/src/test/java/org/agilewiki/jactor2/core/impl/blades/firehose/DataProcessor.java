package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public interface DataProcessor extends Blade {

    AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData);
}
