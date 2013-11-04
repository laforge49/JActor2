package org.agilewiki.jactor2.utilImpl.durable.incDes.collection;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;

/**
 * Creates map entries.
 */
public class MapEntryFactory extends FactoryImpl {

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String keyType, final String valueType)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new MapEntryFactory(actorType, keyType,
                        valueType));
    }

    private final String keyType;
    private final String valueType;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType The jid type.
     */
    public MapEntryFactory(final String jidType, final String keyType,
            final String valueType) {
        super(jidType);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected MapEntryImpl instantiateBlade() {
        return new MapEntryImpl();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public MapEntryImpl newSerializable(final Reactor reactor,
            final Ancestor parent) throws Exception {
        final MapEntryImpl me = (MapEntryImpl) super.newSerializable(reactor,
                parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        final Factory keyFactory = fl.getFactory(keyType);
        final Factory valueFactory = fl.getFactory(valueType);
        me.setFactories(keyFactory, valueFactory);
        return me;
    }
}
