package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.BlockingReactor;

public interface BlockingBlade extends Blade {
    @Override
    BlockingReactor getReactor();
}
