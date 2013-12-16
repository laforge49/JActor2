package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public interface NonBlockingBlade extends Blade {
    @Override
    NonBlockingReactor getReactor();
}
