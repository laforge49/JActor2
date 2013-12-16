package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public interface IsolationBlade extends Blade {
    @Override
    IsolationReactor getReactor();
}
