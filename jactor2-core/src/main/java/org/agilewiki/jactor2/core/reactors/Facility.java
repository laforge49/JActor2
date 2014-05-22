package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;

/**
 * A reactor parent, facilities are named and registered with Plant.
 */
public class Facility extends NonBlockingReactor implements NamedBlade {
    public final String name;

    /**
     * Create a facility with the Plant internal reactor as the parent.
     * @param _name    The name of the facility.
     */
    public Facility(final String _name) throws Exception {
        name = _name;
    }

    /**
     * Create a facility.
     *
     * @param _name    The name of the facility.
     * @param _parentReactor The parent reactor.
     */
    public Facility(final String _name, Facility _parentReactor) throws Exception {
        super(_parentReactor);
        name = _name;
    }

    /**
     * Create a facility with the Plant internal reactor as the parent.
     *
     * @param _name    The name of the facility.
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(final String _name, int _initialOutboxSize, int _initialLocalQueueSize) throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize);
        name = _name;
    }

    /**
     * Create a facility.
     *
     * @param _name    The name of the facility.
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(final String _name, Facility _parentReactor, int _initialOutboxSize, int _initialLocalQueueSize) throws Exception {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
        name = _name;
    }

    @Override
    public String getName() {
        return name;
    }
}
