package org.agilewiki.jactor2.core.reactors;

/**
 * A reactor parent, facilities are named and registered with Plant.
 */
public class Facility extends NonBlockingReactor {
    /**
     * Create a facility with the Plant internal reactor as the parent.
     */
    public Facility() {
    }

    /**
     * Create a facility.
     *
     * @param _parentReactor The parent reactor.
     */
    public Facility(Facility _parentReactor) {
        super(_parentReactor);
    }

    /**
     * Create a facility with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(int _initialOutboxSize, int _initialLocalQueueSize) {
        super(_initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a facility.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(Facility _parentReactor, int _initialOutboxSize, int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }
}
