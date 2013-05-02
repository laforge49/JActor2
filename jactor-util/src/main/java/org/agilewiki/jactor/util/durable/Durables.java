package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.util.JAProperties;
import org.agilewiki.jactor.util.durable.incDes.*;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.app.App;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor.utilImpl.durable.app.AppFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.blist.BListFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap.IntegerBMapFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap.LongBMapFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.bmap.StringBMapFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.collection.tuple.TupleFactory;
import org.agilewiki.jactor.utilImpl.durable.incDes.scalar.flens.*;
import org.agilewiki.jactor.utilImpl.durable.incDes.scalar.vlens.*;

public class Durables {

    public static MailboxFactory createMailboxFactory() throws Exception {
        MailboxFactory mailboxFactory = createMailboxFactory("org.agilewiki.jactor.util.durable", "", "");
        registerFactories(mailboxFactory);
        return mailboxFactory;
    }

    public static MailboxFactory createMailboxFactory(
            final String _bundleName,
            final String _version,
            final String _location) throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        JAProperties properties = new JAProperties(mailboxFactory);
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

        JABooleanImpl.registerFactory(_factoryLocator);
        JAIntegerImpl.registerFactory(_factoryLocator);
        JALongImpl.registerFactory(_factoryLocator);
        JAFloatImpl.registerFactory(_factoryLocator);
        JADoubleImpl.registerFactory(_factoryLocator);

        BoxImpl.registerFactory(_factoryLocator);
        RootImpl.registerFactory(_factoryLocator);
        JAStringImpl.registerFactory(_factoryLocator);
        BytesImpl.registerFactory(_factoryLocator);

        registerListFactory(_factoryLocator, JAList.JASTRING_LIST, JAString.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.BYTES_LIST, Bytes.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.BOX_LIST, Box.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.JALONG_LIST, JALong.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.JAINTEGER_LIST, JAInteger.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.JAFLOAT_LIST, JAFloat.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.JADOUBLE_LIST, JADouble.FACTORY_NAME);
        registerListFactory(_factoryLocator, JAList.JABOOLEAN_LIST, JABoolean.FACTORY_NAME);

        registerStringMapFactory(_factoryLocator, JAMap.STRING_JASTRING_MAP, JAString.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_BYTES_MAP, Bytes.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_BOX_MAP, Box.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_JALONG_MAP, JALong.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_JAINTEGER_MAP, JAInteger.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_JAFLOAT_MAP, JAFloat.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_JADOUBLE_MAP, JADouble.FACTORY_NAME);
        registerStringMapFactory(_factoryLocator, JAMap.STRING_JABOOLEAN_MAP, JABoolean.FACTORY_NAME);


        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JASTRING_MAP, JAString.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_BYTES_MAP, Bytes.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_BOX_MAP, Box.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JALONG_MAP, JALong.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JAINTEGER_MAP, JAInteger.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JAFLOAT_MAP, JAFloat.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JADOUBLE_MAP, JADouble.FACTORY_NAME);
        registerIntegerMapFactory(_factoryLocator, JAMap.INTEGER_JABOOLEAN_MAP, JABoolean.FACTORY_NAME);

        registerLongMapFactory(_factoryLocator, JAMap.LONG_JASTRING_MAP, JAString.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_BYTES_MAP, Bytes.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_BOX_MAP, Box.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_JALONG_MAP, JALong.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_JAINTEGER_MAP, JAInteger.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_JAFLOAT_MAP, JAFloat.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_JADOUBLE_MAP, JADouble.FACTORY_NAME);
        registerLongMapFactory(_factoryLocator, JAMap.LONG_JABOOLEAN_MAP, JABoolean.FACTORY_NAME);

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

    public static void registerAppFactory(final FactoryLocator _factoryLocator,
                                          final Class<?> _appClass,
                                          final String _subActorType) {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new AppFactory(_subActorType) {
            @Override
            protected App instantiateActor() throws Exception {
                return (App) _appClass.newInstance();
            }
        });
    }

    public static void registerAppFactory(final FactoryLocator _factoryLocator,
                                          final Class<?> _appClass,
                                          final String _subActorType,
                                          final String... _actorTypes) {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new AppFactory(_subActorType, _actorTypes) {
            @Override
            protected App instantiateActor() throws Exception {
                return (App) _appClass.newInstance();
            }
        });
    }

    public static FactoryLocator getFactoryLocator(final Mailbox _mailbox) {
        return getFactoryLocator(_mailbox.getMailboxFactory());
    }

    public static FactoryLocator getFactoryLocator(final MailboxFactory _mailboxFactory) {
        return (FactoryLocator) _mailboxFactory.getProperties().getProperty("factoryLocator");
    }

    public static JASerializable newSerializable(final MailboxFactory _mailboxFactory,
                                                 final String _factoryName)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_mailboxFactory),
                _factoryName,
                _mailboxFactory,
                null);
    }

    public static JASerializable newSerializable(final MailboxFactory _mailboxFactory,
                                                 final String _factoryName,
                                                 final Ancestor _parent)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_mailboxFactory),
                _factoryName,
                _mailboxFactory,
                _parent);
    }

    public static JASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_mailbox.getMailboxFactory()),
                _factoryName,
                _mailbox,
                null);
    }

    public static JASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return newSerializable(getFactoryLocator(_mailbox.getMailboxFactory()), _factoryName, _mailbox, _parent);
    }

    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailbox, null);
    }

    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailbox, _parent);
    }

    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailboxFactory.createMailbox(), null);
    }

    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory,
                                                 final Ancestor _parent)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailboxFactory.createMailbox(), _parent);
    }
}
