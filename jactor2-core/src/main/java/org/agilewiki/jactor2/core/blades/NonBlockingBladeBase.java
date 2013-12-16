package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * <p>
 * A blades which does not perform long computations nor otherwise block the thread.
 * </p>
 * <p>
 * This is just a convenience class, as any blades which uses a non-blocking targetReactor
 * is a non-blocking blades.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class NonBlockingBladeSample extends NonBlockingBladeBase {
 *     public NonBlockingBladeSample(final Facility _facility) throws Exception {
 *         super(new NonBlockingReactor(_facility));
 *     }
 * }
 * </pre>
 */
public class NonBlockingBladeBase extends BladeBase implements NonBlockingBlade {

    /**
     * Create a non-blocking blades.
     *
     * @param _nonBlockingReactor A targetReactor for blades which process messages
     *                            quickly and without blocking the thread.
     */
    public void initialize(final NonBlockingReactor _nonBlockingReactor)
            throws Exception {
        _initialize(_nonBlockingReactor);
    }

    @Override
    public NonBlockingReactor getReactor() {
        return (NonBlockingReactor) super.getReactor();
    }
}