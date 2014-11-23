package org.agilewiki.jactor2.core.impl.blades.firehose;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.requests.AIOp;

public interface DataProcessor extends Blade {

    AIOp<Void> processDataAOp(final FirehoseData _firehoseData);
}
