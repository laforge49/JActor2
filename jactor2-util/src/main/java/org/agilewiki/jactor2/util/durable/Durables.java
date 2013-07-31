package org.agilewiki.jactor2.util.durable;

import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.context.MailboxFactory;
import org.agilewiki.jactor2.core.context.Properties;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.JAProperties;
import org.agilewiki.jactor2.util.durable.app.App;
import org.agilewiki.jactor2.util.durable.incDes.*;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.app.AppFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.blist.BListFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap.IntegerBMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap.LongBMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap.StringBMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.tuple.TupleFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens.*;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.*;

/**
 * Static methods for accessing durable capabilities.
 */
public final class Durables {

    /**
     * Creates a mailboxFactory with a factoryLocator that supports all the pre-defined factories.
     *
     * @return A mailbox factory whose properties include the factoryLocator.
     */
    public static DefaultMailboxFactory createMailboxFactory() throws Exception {
        DefaultMailboxFactory mailboxFactory = new DefaultMailboxFactory();
        FactoryLocator factoryLocator =
                createFactoryLocator(mailboxFactory, "org.agilewiki.jactor2.util.durable", "", "");
        registerFactories(factoryLocator);
        return mailboxFactory;
    }

    /**
     * Create a factoryLocator and add it to the mailboxFactory properties.
     *
     * @param _mailboxFactory A mailboxFactory.
     * @param _bundleName     The name of the OSGi bundle or an empty string.
     * @param _version        The version of the OSGi bundle or an empty string.
     * @param _location       The location of the OSGi bundle or an empty string.
     * @return The new factoryLocator.
     */
    public static FactoryLocator createFactoryLocator(
            final MailboxFactory _mailboxFactory,
            final String _bundleName,
            final String _version,
            final String _location) throws Exception {
        Properties properties = _mailboxFactory.getProperties();
        if (properties == null) {
            properties = new JAProperties(_mailboxFactory);
        }
        FactoryLocatorImpl factoryLocator = new FactoryLocatorImpl();
        factoryLocator.configure(_bundleName, _version, _location);
        properties.putProperty("factoryLocator", factoryLocator);
        return factoryLocator;
    }

    /**
     * Returns the factoryLocator from the mailboxFactory's factoryLocator property.
     *
     * @param _mailbox A mailbox.
     * @return The factoryLocator for that mailbox.
     */
    public static FactoryLocator getFactoryLocator(final Mailbox _mailbox) {
        return getFactoryLocator(_mailbox.getMailboxFactory());
    }

    /**
     * Returns the factoryLocator from the mailboxFactory's factoryLocator property.
     *
     * @param _mailboxFactory A mailboxFactory.
     * @return The factoryLocator for that mailboxFactory.
     */
    public static FactoryLocator getFactoryLocator(final MailboxFactory _mailboxFactory) {
        return (FactoryLocator) _mailboxFactory.getProperties().getProperty("factoryLocator");
    }

    /**
     * Register all the pre-defined factories.
     *
     * @param _factoryLocator The factoryLocator that will hold the factories.
     */
    public static void registerFactories(final FactoryLocator _factoryLocator) throws FactoryLocatorClosedException {

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
    }

    /**
     * Register a list factory.
     *
     * @param _factoryLocator   The factoryLocator that will hold the factory.
     * @param _listFactoryName  The new list type.
     * @param _valueFactoryName The list entry type.
     */
    public static void registerListFactory(final FactoryLocator _factoryLocator,
                                           final String _listFactoryName,
                                           final String _valueFactoryName) throws FactoryLocatorClosedException {
        BListFactory.registerFactory(_factoryLocator, _listFactoryName, _valueFactoryName);
    }

    /**
     * Register the factory of a map with keys that are strings.
     *
     * @param _factoryLocator   The factoryLocator that will hold the factory.
     * @param _mapFactoryName   The new string map type.
     * @param _valueFactoryName The map entry type.
     */
    public static void registerStringMapFactory(final FactoryLocator _factoryLocator,
                                                final String _mapFactoryName,
                                                final String _valueFactoryName) throws FactoryLocatorClosedException {
        StringBMapFactory.registerFactory(_factoryLocator, _mapFactoryName, _valueFactoryName);
    }

    /**
     * Register the factory of a map with keys that are integers.
     *
     * @param _factoryLocator   The factoryLocator that will hold the factory.
     * @param _mapFactoryName   The new integer map type.
     * @param _valueFactoryName The map entry type.
     */
    public static void registerIntegerMapFactory(final FactoryLocator _factoryLocator,
                                                 final String _mapFactoryName,
                                                 final String _valueFactoryName) throws FactoryLocatorClosedException {
        IntegerBMapFactory.registerFactory(_factoryLocator, _mapFactoryName, _valueFactoryName);
    }

    /**
     * Register the factory of a map with keys that are longs.
     *
     * @param _factoryLocator   The factoryLocator that will hold the factory.
     * @param _mapFactoryName   The new long map type.
     * @param _valueFactoryName The map entry type.
     */
    public static void registerLongMapFactory(final FactoryLocator _factoryLocator,
                                              final String _mapFactoryName,
                                              final String _valueFactoryName) throws FactoryLocatorClosedException {
        LongBMapFactory.registerFactory(_factoryLocator, _mapFactoryName, _valueFactoryName);
    }

    /**
     * Register the factory of a union.
     *
     * @param _factoryLocator    The factoryLocator that will hold the factory.
     * @param _unionFactoryName  The new union type.
     * @param _valueFactoryNames The types of the possible values of the union.
     */
    public static void registerUnionFactory(final FactoryLocator _factoryLocator,
                                            final String _unionFactoryName,
                                            final String... _valueFactoryNames) throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator, _unionFactoryName, _valueFactoryNames);
    }

    /**
     * Register the factory of a tuple.
     *
     * @param _factoryLocator    The factoryLocator that will hold the factory.
     * @param _tupleFactoryName  The new tuple type.
     * @param _valueFactoryNames The types of the values of the tuple.
     */
    public static void registerTupleFactory(final FactoryLocator _factoryLocator,
                                            final String _tupleFactoryName,
                                            final String... _valueFactoryNames) throws FactoryLocatorClosedException {
        TupleFactory.registerFactory(_factoryLocator, _tupleFactoryName, _valueFactoryNames);
    }

    /**
     * Register the factory of a serializable application.
     *
     * @param _factoryLocator The factoryLocator that will hold the factory.
     * @param _appClass       The application class.
     * @param _appFactoryName The application type.
     */
    public static void registerAppFactory(final FactoryLocator _factoryLocator,
                                          final Class<?> _appClass,
                                          final String _appFactoryName) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new AppFactory(_appFactoryName) {
            @Override
            protected App instantiateActor() throws Exception {
                return (App) _appClass.newInstance();
            }
        });
    }

    /**
     * Register the factory of a serializable application.
     *
     * @param _factoryLocator    The factoryLocator that will hold the factory.
     * @param _appClass          The application class.
     * @param _appFactoryName    The application type.
     * @param _valueFactoryNames The types of the durable values of the application.
     */
    public static void registerAppFactory(final FactoryLocator _factoryLocator,
                                          final Class<?> _appClass,
                                          final String _appFactoryName,
                                          final String... _valueFactoryNames) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new AppFactory(_appFactoryName, _valueFactoryNames) {
            @Override
            protected App instantiateActor() throws Exception {
                return (App) _appClass.newInstance();
            }
        });
    }

    /**
     * Create a new serializable object.
     *
     * @param _factoryLocator The factoryLocator.
     * @param _factoryName    The type of object to be created.
     * @param _mailbox        The mailbox to be used by the new object.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailbox, null);
    }

    /**
     * Create a new serializable object.
     *
     * @param _factoryLocator The factoryLocator.
     * @param _factoryName    The type of object to be created.
     * @param _mailbox        The mailbox to be used by the new object.
     * @param _parent         The dependency to be injected, or null.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailbox, _parent);
    }

    /**
     * Create a new serializable object and a new mailbox to be used by that serializable object.
     *
     * @param _factoryLocator The factoryLocator.
     * @param _factoryName    The type of object to be created.
     * @param _mailboxFactory The mailboxFactory to be used to create the new mailbox.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailboxFactory.createNonBlockingMailbox(), null);
    }

    /**
     * Create a new serializable object and a new mailbox to be used by that serializable object.
     *
     * @param _factoryLocator The factoryLocator.
     * @param _factoryName    The type of object to be created.
     * @param _mailboxFactory The mailboxFactory to be used to create the new mailbox.
     * @param _parent         The dependency to be injected, or null.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory,
                                                 final Ancestor _parent)
            throws Exception {
        return ((FactoryLocatorImpl) _factoryLocator).newSerializable(_factoryName, _mailboxFactory.createNonBlockingMailbox(), _parent);
    }

    /**
     * Create a new serializable object and a new mailbox to be used by that serializable object.
     *
     * @param _factoryName    The type of object to be created.
     * @param _mailboxFactory The mailboxFactory to be used to create the new mailbox
     *                        and which has a factoryLocator property.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final MailboxFactory _mailboxFactory,
                                                 final String _factoryName)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_mailboxFactory),
                _factoryName,
                _mailboxFactory,
                null);
    }

    /**
     * Create a new serializable object and a new mailbox to be used by that serializable object.
     *
     * @param _factoryName    The type of object to be created.
     * @param _mailboxFactory The mailboxFactory to be used to create the new mailbox
     *                        and which has a factoryLocator property.
     * @param _parent         The dependency to be injected, or null.
     * @return A new serializable object.
     */
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

    /**
     * Create a new serializable object.
     *
     * @param _factoryName The type of object to be created.
     * @param _mailbox     The mailbox to be used by the new serializable object
     *                     and whose mailboxFactory has a factoryLocator property.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_mailbox.getMailboxFactory()),
                _factoryName,
                _mailbox,
                null);
    }

    /**
     * Create a new serializable object.
     *
     * @param _factoryName The type of object to be created.
     * @param _mailbox     The mailbox to be used by the new serializable object
     *                     and whose mailboxFactory has a factoryLocator property.
     * @param _parent      The dependency to be injected, or null.
     * @return A new serializable object.
     */
    public static JASerializable newSerializable(final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return newSerializable(getFactoryLocator(_mailbox.getMailboxFactory()), _factoryName, _mailbox, _parent);
    }
}
