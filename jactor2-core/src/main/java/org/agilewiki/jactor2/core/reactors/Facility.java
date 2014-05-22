package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * A reactor parent, facilities are named and registered with Plant.
 */
public class Facility extends NonBlockingReactor implements NamedBlade {
    public final String name;

    private ISMap<NamedBlade> namedBlades = PlantBase.createISMap();

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
    public Facility(final String _name, Void _parentReactor, int _initialOutboxSize, int _initialLocalQueueSize) throws Exception {
        super(null, _initialOutboxSize, _initialLocalQueueSize);
        name = _name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void validateName(final String _name) {
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
        }
    }

    /**
     * Returns the ISMap of named blades.
     *
     * @return The ISMap.
     */
    public ISMap<NamedBlade> getNamedBlades() {
        return namedBlades;
    }

    /**
     * Returns the named blade.
     *
     * @param _name    The name of the blade.
     * @return The Blade, or null.
     */
    public NamedBlade getNamedBlade(final String _name) {
        return namedBlades.get(_name);
    }

    /**
     * A request to unregister the named blade. The result of the request is
     * the unregistered blade, or null.
     *
     * @param _name    The name of the blade.
     * @return The request to unregister.
     */
    public SyncRequest<NamedBlade> unregisterNamedBlade(final String _name) {
        return new SyncRequest<NamedBlade>(Facility.this) {
            @Override
            public NamedBlade processSyncRequest() throws Exception {
                NamedBlade removed = namedBlades.get(_name);
                if (removed != null)
                    namedBlades = namedBlades.minus(_name);
                return removed;
            }
        };
    }

    /**
     * A request to register a blade. The request throws an IllegalStateException
     * if the name is a duplicate.
     *
     * An IllegalArgumentException is thrown if the name is invalid.
     *
     * @param _name     The name used to register the blade.
     * @param _blade    The blade being registered.
     * @return The request to register.
     */
    public SyncRequest<Void> registerNamedBlade(final String _name, final NamedBlade _blade) {
        validateName(_name);
        return new SyncRequest<Void>(Facility.this) {
            @Override
            public Void processSyncRequest() throws Exception {
                NamedBlade oldBlade = namedBlades.get(_name);
                if (oldBlade != null)
                    throw new IllegalArgumentException("duplicate blade name");
                namedBlades = namedBlades.plus(_name, _blade);
                return null;
            }
        };
    }
}
