package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * A blade which does not perform long computations nor otherwise block the thread.
 * </p>
 * This is just a convenience class, as any blade which uses a non-blocking targetReactor
 * is a non-blocking blade.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class NonBlockingBladeSample extends NonBlockingBlade {
 *     public NonBlockingBladeSample(final Facility _facility) throws Exception {
 *         super(new NonBlockingReactor(_facility));
 *     }
 * }
 * </pre>
 */
public class NonBlockingBlade extends BladeBase {

    /**
     * Create a non-blocking blade.
     *
     * @param _nonBlockingReactor A targetReactor for blades which process messages
     *                            quickly and without blocking the thread.
     */
    public NonBlockingBlade(final NonBlockingReactor _nonBlockingReactor)
            throws Exception {
        initialize(_nonBlockingReactor);
    }
}
