package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.requests.AOp;

public interface DataProcessor extends Blade {

    AOp<Void> processDataAOp(final FirehoseData _firehoseData);
}
