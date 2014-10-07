package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions.TSSMap;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

import java.util.SortedMap;

/**
 * A reactor parent, facilities are named and registered with Plant.
 */
public class Facility extends NonBlockingReactor implements NamedBlade {
    public final String name;

    private volatile SortedMap<String, NamedBlade> namedBlades = new TSSMap();
    protected TSSMap<NamedBlade> namedBladesTransmutable = new TSSMap();

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

    public SortedMap<String, NamedBlade> getNamedBlades() {
        return namedBlades;
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
            protected NamedBlade processSyncOperation(RequestImpl _requestImpl) throws Exception {
                final NamedBlade removed = namedBladesTransmutable.remove(_name);
                if (removed != null) {
                    namedBlades = namedBladesTransmutable.createUnmodifiable();
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
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                String name = _blade.getName();
                final NamedBlade oldBlade = namedBladesTransmutable.get(name);
                if (oldBlade != null) {
                    throw new IllegalArgumentException("duplicate blade name");
                }
                namedBladesTransmutable.put(name, _blade);
                namedBlades = namedBladesTransmutable.createUnmodifiable();
                registrationNotifier.signalContent(
                        new RegistrationNotification(Facility.this, name, _blade), Facility.this);
                return null;
            }
        };
    }
}
