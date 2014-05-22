package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.plant.PlantImpl;

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
        validateName(_name);
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
        validateName(_name);
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
        validateName(_name);
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
        validateName(_name);
        name = _name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void validateName(final String _name) throws Exception {
        if (_name == null) {
            throw new IllegalArgumentException("name may not be null");
        }
        if (_name.length() == 0) {
            throw new IllegalArgumentException("name may not be empty");
        }
        if (_name.contains(" ")) {
            throw new IllegalArgumentException("name may not contain spaces: "
                    + _name);
        }
        if (_name.contains("~")) {
            throw new IllegalArgumentException("name may not contain ~: "
                    + _name);
        }
        if (_name.equals(PlantImpl.PLANT_INTERNAL_FACILITY_NAME)) {
            if (getParentReactor() != null)
                throw new IllegalArgumentException("name may not be " + PlantImpl.PLANT_INTERNAL_FACILITY_NAME);
        //} else if (MPlant.getFacility(_name) != null) {
        //    throw new IllegalStateException("facility by that name already exists");
        }
    }
}
