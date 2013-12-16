package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

public interface ThreadBoundBlade extends Blade {
    @Override
    ThreadBoundReactor getReactor();
}
