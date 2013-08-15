package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.tuple;

import org.agilewiki.jactor2.core.processing.Mailbox;
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

    public static void registerFactory(FactoryLocator _factoryLocator,
                                       String subActorType, String... actorTypes) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new TupleFactory(subActorType, actorTypes));
    }

    private String[] jidTypes;

    /**
     * Create a JLPCActorFactory.
     *
     * @param subJidType The jid type.
     * @param jidTypes   The element types.
     */
    protected TupleFactory(String subJidType, String... jidTypes) {
        super(subJidType);
        this.jidTypes = jidTypes;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected TupleImpl instantiateActor() {
        return new TupleImpl();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public TupleImpl newSerializable(Mailbox mailbox, Ancestor parent)
            throws Exception {
        TupleImpl tj = (TupleImpl) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        Factory[] afs = new FactoryImpl[jidTypes.length];
        int i = 0;
        while (i < jidTypes.length) {
            afs[i] = fl.getFactory(jidTypes[i]);
            i += 1;
        }
        tj.tupleFactories = afs;
        return tj;
    }
}
