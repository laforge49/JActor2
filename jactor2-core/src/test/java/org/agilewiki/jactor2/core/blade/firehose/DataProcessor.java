package org.agilewiki.jactor2.core.blade.firehose;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public interface DataProcessor extends Blade {

    AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData);
}
