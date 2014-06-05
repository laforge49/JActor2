package org.agilewiki.jactor2.core.blades.ismTransactions;

import java.util.Map;

import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Supports validation and notifications of changes to an ISMap.
 */
public class ISMReference<VALUE> extends ImmutableReference<ISMap<VALUE>> {

    public static <V> ISMap<V> empty() {
        return PlantBase.createISMap();
    }

    public static <V> ISMap<V> singleton(final String key, final V value) {
        return PlantBase.createISMap(key, value);
    }

    public static <V> ISMap<V> from(final Map<String, V> m) {
        return PlantBase.createISMap(m);
    }

    /**
     * The RequestBus used to validate the changes made by a transaction.
     */
    public final RequestBus<ImmutableChanges<VALUE>> validationBus;

    /**
     * The RequestBus used to signal the changes made by a validated transaction.
     */
    public final RequestBus<ImmutableChanges<VALUE>> changeBus;

    @SuppressWarnings("unchecked")
    public ISMReference() throws Exception {
        this((ISMap<VALUE>) empty());
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _immutable    The immutable data structure to be operated on.
     */
    public ISMReference(final ISMap<VALUE> _immutable) throws Exception {
        super(_immutable);
        final NonBlockingReactor parentReactor = (NonBlockingReactor) getReactor()
                .getParentReactor();
        validationBus = new RequestBus<ImmutableChanges<VALUE>>(parentReactor);
        changeBus = new RequestBus<ImmutableChanges<VALUE>>(parentReactor);
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _reactor      The blade's reactor.
     */
    @SuppressWarnings("unchecked")
    public ISMReference(final IsolationReactor _reactor) {
        this((ISMap<VALUE>) empty(), _reactor);
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _immutable    The immutable data structure to be operated on.
     * @param _reactor      The blade's reactor.
     */
    public ISMReference(final ISMap<VALUE> _immutable,
            final IsolationReactor _reactor) {
        super(_immutable, _reactor);
        final NonBlockingReactor parentReactor = (NonBlockingReactor) _reactor
                .getParentReactor();
        validationBus = new RequestBus<ImmutableChanges<VALUE>>(parentReactor);
        changeBus = new RequestBus<ImmutableChanges<VALUE>>(parentReactor);
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _parentReactor    The parent of the blade's reactor.
     */
    @SuppressWarnings("unchecked")
    public ISMReference(final NonBlockingReactor _parentReactor)
            throws Exception {
        this((ISMap<VALUE>) empty(), _parentReactor);
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _immutable    The immutable data structure to be operated on.
     * @param _parentReactor    The parent of the blade's reactor.
     */
    public ISMReference(final ISMap<VALUE> _immutable,
            final NonBlockingReactor _parentReactor) throws Exception {
        super(_immutable, _parentReactor);
        validationBus = new RequestBus<ImmutableChanges<VALUE>>(_parentReactor);
        changeBus = new RequestBus<ImmutableChanges<VALUE>>(_parentReactor);
    }
}
