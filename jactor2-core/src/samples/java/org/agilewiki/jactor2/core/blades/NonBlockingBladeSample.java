package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class NonBlockingBladeSample extends NonBlockingBladeBase {
    public NonBlockingBladeSample(final Facility _facility) throws Exception {
        super(new NonBlockingReactor(_facility));
    }
}
