package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.tuple;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;

/**
 * Creates a TupleImpl.
 */
public class TupleFactory extends FactoryImpl {

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String subActorType, final String... actorTypes)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new TupleFactory(subActorType, actorTypes));
    }

    private final String[] jidTypes;

    /**
     * Create a JLPCActorFactory.
     *
     * @param subJidType The jid type.
     * @param jidTypes   The element types.
     */
    protected TupleFactory(final String subJidType, final String... jidTypes) {
        super(subJidType);
        this.jidTypes = jidTypes;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected TupleImpl instantiateBlade() {
        return new TupleImpl();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public TupleImpl newSerializable(final Reactor reactor,
            final Ancestor parent) throws Exception {
        final TupleImpl tj = (TupleImpl) super.newSerializable(reactor, parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        final Factory[] afs = new FactoryImpl[jidTypes.length];
        int i = 0;
        while (i < jidTypes.length) {
            afs[i] = fl.getFactory(jidTypes[i]);
            i += 1;
        }
        tj.tupleFactories = afs;
        return tj;
    }
}
