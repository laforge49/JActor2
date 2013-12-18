package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

public interface SwingBoundBlade extends Blade {
    @Override
    SwingBoundReactor getReactor();
}
