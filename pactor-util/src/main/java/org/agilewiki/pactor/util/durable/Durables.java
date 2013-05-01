package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.PAProperties;
import org.agilewiki.pactor.util.durable.incDes.*;
import org.agilewiki.pactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.blist.BListFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.bmap.IntegerBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.bmap.LongBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.bmap.StringBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.collection.tuple.TupleFactory;
import org.agilewiki.pactor.utilImpl.durable.incDes.scalar.flens.*;
import org.agilewiki.pactor.utilImpl.durable.incDes.scalar.vlens.*;

public class Durables {

    public static MailboxFactory createMailboxFactory() throws Exception {
        MailboxFactory mailboxFactory = createMailboxFactory("org.agilewiki.pactor.util.durable");
        registerFactories(mailboxFactory);
        return mailboxFactory;
    }

    public static MailboxFactory createMailboxFactory(final String _bundleName) throws Exception {
        return createMailboxFactory(_bundleName, "", "");
    }

    public static MailboxFactory createMailboxFactory(
            final String _bundleName,
            final String _version,
            final String _location) throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        PAProperties properties = new PAProperties(mailboxFactory);
        FactoryLocatorImpl factoryLocator = new FactoryLocatorImpl();
        factoryLocator.configure(_bundleName, _version, _location);
        properties.putProperty("factoryLocator", factoryLocator);
        return mailboxFactory;
    }

    public static void registerFactories(final MailboxFactory _mailboxFactory) {
        FactoryLocator factoryLocator = getFactoryLocator(_mailboxFactory);
        registerFactories(factoryLocator);
    }

    public static void registerFactories(final FactoryLocator _factoryLocator) {
        IncDesFactory.registerFactory(_factoryLocator);

        PABooleanImpl.registerFactory(_factoryLocator);
        PAIntegerImpl.registerFactory(_factoryLocator);
        PALongImpl.registerFactory(_factoryLocator);
        PAFloatImpl.registerFactory(_factoryLocator);
        PADoubleImpl.registerFactory(_factoryLocator);

        BoxImpl.registerFactory(_factoryLocator);
        RootImpl.registerFactory(_factoryLocator);
        PAStringImpl.registerFactory(_factoryLocator);
        BytesImpl.registerFactory(_factoryLocator);

        registerListFactory(_factoryLocator, PAList.PASTRING_LIST, PAString.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.BYTES_LIST, Bytes.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.BOX_LIST, Box.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.PALONG_LIST, PALong.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.PAINTEGER_LIST, PAInteger.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.PAFLOAT_LIST, PAFloat.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.PADOUBLE_LIST, PADouble.FACTORY_NAME);
        registerListFactory(_factoryLocator, PAList.PABOOLEAN_LIST, PABoolean.FACTORY_NAME);

        registerStringMapFactory(_factoryLocator, PAMap.STRING_PASTRING_MAP, PAString.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_BYTES_MAP, Bytes.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_BOX_MAP, Box.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_PALONG_MAP, PALong.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, PAMap.STRING_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);


        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PASTRING_MAP, PAString.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_BYTES_MAP, Bytes.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_BOX_MAP, Box.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PALONG_MAP, PALong.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, PAMap.INTEGER_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);

        registerLongMapFactory(_factoryLocator, PAMap.LONG_PASTRING_MAP, PAString.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_BYTES_MAP, Bytes.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_BOX_MAP, Box.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_PALONG_MAP, PALong.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_PAINTEGER_MAP, PAInteger.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_PAFLOAT_MAP, PAFloat.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_PADOUBLE_MAP, PADouble.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, PAMap.LONG_PABOOLEAN_MAP, PABoolean.FACTORY_NAME);

        StringBMapFactory.registerFactories(_factoryLocator);
    }

    public static void registerListFactory(final FactoryLocator _factoryLocator,
                                           final String _actorType,
                                           final String _valueType) {
        BListFactory.registerFactory(_factoryLocator, _actorType, _valueType);
    }

    public static void registerStringMapFactory(final FactoryLocator _factoryLocator,
                                                final String _actorType,
                                                final String _valueType) {
        StringBMapFactory.registerFactory(_factoryLocator, _actorType, _valueType);
    }

    public static void registerIntegerMapFactory(final FactoryLocator _factoryLocator,
                                                 final String _actorType,
                                                 final String _valueType) {
        IntegerBMapFactory.registerFactory(_factoryLocator, _actorType, _valueType);
    }

    public static void registerLongMapFactory(final FactoryLocator _factoryLocator,
                                              final String _actorType,
                                              final String _valueType) {
        LongBMapFactory.registerFactory(_factoryLocator, _actorType, _valueType);
    }

    public static void registerUnionFactory(final FactoryLocator _factoryLocator,
                                            final String _subActorType,
                                            final String... _actorTypes) {
        UnionImpl.registerFactory(_factoryLocator, _subActorType, _actorTypes);
    }

    public static void registerTupleFactory(final FactoryLocator _factoryLocator,
                                            final String _subActorType,
                                            final String... _actorTypes) {
        TupleFactory.registerFactory(_factoryLocator, _subActorType, _actorTypes);
    }

    public static FactoryLocator getFactoryLocator(final Mailbox _mailbox) {
        return getFactoryLocator(_mailbox.getMailboxFactory());
    }

    public static FactoryLocator getFactoryLocator(final MailboxFactory _mailboxFactory) {
        return (FactoryLocator) _mailboxFactory.getProperties().getProperty("factoryLocator");
    }

    public static PASerializable newSerializable(final MailboxFactory _mailboxFactory,
                                                 final String _factoryName) {
        return newSerializable(
                getFactoryLocator(_mailboxFactory),
                _factoryName,
                _mailboxFactory,
                null);
    }

    public static PASerializable newSerializable(final MailboxFactory _mailboxFactory,
                                                 final String _factoryName,
                                                 final Ancestor _parent) {
        return newSerializable(
                getFactoryLocator(_mailboxFactory),
                _factoryName,
                _mailboxFactory,
                _parent);
    }

    public static PASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox) {
        return newSerializable(
                getFactoryLocator(_mailbox.getMailboxFactory()),
                _factoryName,
                _mailbox,
                null);
    }

    public static PASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent) {
        return newSerializable(getFactoryLocator(_mailbox.getMailboxFactory()), _factoryName, _mailbox, _parent);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox) {
        return _factoryLocator.newSerializable(_factoryName, _mailbox, null);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent) {
        return _factoryLocator.newSerializable(_factoryName, _mailbox, _parent);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory) {
        return _factoryLocator.newSerializable(_factoryName, _mailboxFactory.createMailbox(), null);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory,
                                                 final Ancestor _parent) {
        return _factoryLocator.newSerializable(_factoryName, _mailboxFactory.createMailbox(), _parent);
    }
}
