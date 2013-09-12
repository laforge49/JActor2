package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class NonBlockingBladeSample extends NonBlockingBlade {
    public NonBlockingBladeSample(final Facility _facility) throws Exception {
        super(new NonBlockingReactor(_facility));
    }
}
