package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMReference;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMUpdateTransaction;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMap;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * A reactor parent, facilities are named and registered with Plant.
 */
public class Facility extends NonBlockingReactor implements NamedBlade {
    public final String name;

    protected ISMap<NamedBlade> namedBlades = PlantBase.createISMap();

    public final RequestBus<RegistrationNotification> registrationNotifier;

    /**
     * Create a facility with the Plant internal reactor as the parent.
     *
     * @param _name The name of the facility.
     */
    public Facility(final String _name) throws Exception {
        name = _name;
        registrationNotifier = new RequestBus<RegistrationNotification>(this);
    }

    /**
     * Create a facility.
     *
     * @param _name          The name of the facility.
     * @param _parentReactor The parent reactor.
     */
    public Facility(final String _name, final Facility _parentReactor)
            throws Exception {
        super(_parentReactor);
        name = _name;
        registrationNotifier = new RequestBus<RegistrationNotification>(this);
    }

    /**
     * Create a facility with the Plant internal reactor as the parent.
     *
     * @param _name                  The name of the facility.
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(final String _name, final int _initialOutboxSize,
                    final int _initialLocalQueueSize) throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize);
        name = _name;
        registrationNotifier = new RequestBus<RegistrationNotification>(this);
    }

    /**
     * Create a facility.
     *
     * @param _name                  The name of the facility.
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize Initial size of the local input queue.
     */
    public Facility(final String _name, final Void _parentReactor,
                    final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        super(null, _initialOutboxSize, _initialLocalQueueSize);
        name = _name;
        registrationNotifier = new RequestBus<RegistrationNotification>(this);
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
            if (getParentReactor() != null) {
                throw new IllegalArgumentException("name may not be "
                        + PlantImpl.PLANT_INTERNAL_FACILITY_NAME);
            }
        }
    }

    /**
     * Returns the ISMap of named blades.
     *
     * @return The ISMap.
     */
    public ISMap<NamedBlade> getBlades() {
        return namedBlades;
    }

    /**
     * Returns the named blade.
     *
     * @param _name The name of the blade.
     * @return The Blade, or null.
     */
    public NamedBlade getBlade(final String _name) {
        return namedBlades.get(_name);
    }

    /**
     * Returns true if the blade is registered.
     *
     * @param _name The name of the blade.
     * @return True when the blade is registered.
     */
    public boolean isRegisteredBlade(final String _name) {
        return namedBlades.containsKey(_name);
    }

    /**
     * A request to unregister the named blade. The result of the request is
     * the unregistered blade, or null.
     *
     * @param _name The name of the blade.
     * @return The request to unregister.
     */
    public SOp<NamedBlade> unregisterBladeSOp(final String _name) {
        return new SOp<NamedBlade>("unregisterBlade", Facility.this) {
            @Override
            public NamedBlade processSyncOperation(RequestImpl _requestImpl) throws Exception {
                final NamedBlade removed = namedBlades.get(_name);
                if (removed != null) {
                    namedBlades = namedBlades.minus(_name);
                    registrationNotifier.signalContent(
                            new RegistrationNotification(Facility.this, _name, null), Facility.this);
                }
                return removed;
            }
        };
    }

    /**
     * A request to register a blade. The request throws an IllegalStateException
     * if the name is a duplicate.
     * An IllegalArgumentException is thrown if the name is invalid.
     *
     * @param _blade The blade being registered.
     * @return The request to register.
     */
    public SOp<Void> registerBladeSOp(final NamedBlade _blade) {
        String name = _blade.getName();
        validateName(name);
        return new SOp<Void>("registerBlade", getReactor()) {
            @Override
            public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                String name = _blade.getName();
                final NamedBlade oldBlade = namedBlades.get(name);
                if (oldBlade != null) {
                    throw new IllegalArgumentException("duplicate blade name");
                }
                namedBlades = namedBlades.plus(name, _blade);
                registrationNotifier.signalContent(
                        new RegistrationNotification(Facility.this, name, _blade), Facility.this);
                return null;
            }
        };
    }
}
