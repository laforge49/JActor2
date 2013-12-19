package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

public interface SwingBoundBlade extends Blade {
    @Override
    SwingBoundReactor getReactor();
}
