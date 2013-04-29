package org.agilewiki.pactor.utilImpl.durable.collection.smap;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.*;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.collection.MapEntryFactory;

/**
 * Creates IntegerSMap's.
 */
public class IntegerSMapFactory extends FactoryImpl {

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        registerFactory(_factoryLocator, PAMap.INTEGER_PASTRING_MAP, PAString.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_BYTES_MAP, Bytes.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_BOX_MAP, Box.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_PALONG_MAP, PALong.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerFactory(_factoryLocator, PAMap.INTEGER_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);
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
        factoryLocator.registerFactory(new MapEntryFactory(
                "E." + actorType, PAInteger.FACTORY_NAME, valueType));
        factoryLocator.registerFactory(new IntegerSMapFactory(
                actorType, valueType, initialCapacity));
    }

    private String valueType;
    private int initialCapacity = 10;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     */
    protected IntegerSMapFactory(String jidType, String valueType, int initialCapacity) {
        super(jidType);
        this.valueType = valueType;
        this.initialCapacity = initialCapacity;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected IntegerSMap instantiateActor() {
        return new IntegerSMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param mailbox The mailbox of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public IntegerSMap newSerializable(Mailbox mailbox, Ancestor parent) {
        IntegerSMap imj = (IntegerSMap) super.newSerializable(mailbox, parent);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        imj.valueFactory = fl.getFactory(valueType);
        imj.initialCapacity = initialCapacity;
        return imj;
    }
}
