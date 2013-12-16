package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

public interface SwingBoundBlade extends Blade {
    @Override
    ThreadBoundReactor getReactor();
}
