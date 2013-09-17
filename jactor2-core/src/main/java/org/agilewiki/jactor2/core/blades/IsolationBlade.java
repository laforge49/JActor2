package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * An isolation blade processes requests from other blades one at a time,
 * starting a new request only when a result is returned for the previous
 * one.
 * </p>
 * This is just a convenience class, as any blade which uses an isolation targetReactor
 * is an isolation blade.
 */
public class IsolationBlade extends BladeBase {

    /**
     * Create an isolation blade.
     *
     * @param _facility A set of resources, including a thread pool, for use
     *                  by reactors and their blades.
     */
    public IsolationBlade(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }
}
