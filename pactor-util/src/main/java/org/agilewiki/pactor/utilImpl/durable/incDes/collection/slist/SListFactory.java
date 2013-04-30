package org.agilewiki.pactor.utilImpl.durable.incDes.collection.slist;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.incDes.*;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

/**
 * Creates ListJids.
 */
public class SListFactory extends FactoryImpl {

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        registerFactory(_factoryLocator, PAList.PASTRING_LIST, PAString.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.BYTES_LIST, Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.BOX_LIST, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PALONG_LIST, PALong.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PAINTEGER_LIST, PAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PAFLOAT_LIST, PAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PADOUBLE_LIST, PADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, PAList.PABOOLEAN_LIST, PABoolean.FACTORY_NAME);
    }

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType) {
        registerFactory(factoryLocator, actorType, valueType, 10);
    }

    public static void registerFactory(FactoryLocator factoryLocator,
                                       String actorType,
                                       String valueType,
                                       int initialCapacity) {
        factoryLocator.registerFactory(new SListFactory(
                actorType, valueType, initialCapacity));
    }

    private String entryType;
    private int initialCapacity;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType         The jid type.
     * @param entryType       The entry type.
     * @param initialCapacity The initial capacity.
     */
    protected SListFactory(String jidType, String entryType, int initialCapacity) {
        super(jidType);
        this.entryType = entryType;
        this.initialCapacity = initialCapacity;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected SList instantiateActor() {
        return new SList();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public SList newSerializable(Mailbox mailbox, Ancestor parent) {
        SList lj = (SList) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        lj.entryFactory = fl.getFactory(entryType);
        lj.initialCapacity = initialCapacity;
        return lj;
    }
}
