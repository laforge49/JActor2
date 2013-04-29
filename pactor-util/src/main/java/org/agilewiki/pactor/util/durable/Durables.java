package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.PAProperties;
import org.agilewiki.pactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.pactor.utilImpl.durable.IncDesFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.blist.BListFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.bmap.IntegerBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.bmap.LongBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.bmap.StringBMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.slist.SListFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.smap.IntegerSMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.smap.LongSMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.smap.StringSMapFactory;
import org.agilewiki.pactor.utilImpl.durable.collection.tuple.TupleFactory;
import org.agilewiki.pactor.utilImpl.durable.scalar.flens.*;
import org.agilewiki.pactor.utilImpl.durable.scalar.vlens.*;

public class Durables {

    /**
     * Size of a boolean in bytes.
     */
    public final static int BOOLEAN_LENGTH = 1;

    /**
     * Size of an int in bytes.
     */
    public final static int INT_LENGTH = 4;

    /**
     * Size of a long in bytes.
     */
    public final static int LONG_LENGTH = 8;

    /**
     * Size of an float in bytes.
     */
    public final static int FLOAT_LENGTH = 4;

    /**
     * Size of an double in bytes.
     */
    public final static int DOUBLE_LENGTH = 8;

    public static MailboxFactory createMailboxFactory() throws Exception {
        MailboxFactory mailboxFactory = createMailboxFactory("org.agilewiki.pactor.util.durable");
        registerFactories(mailboxFactory);
        return mailboxFactory;
    }

    public static void registerFactories(final MailboxFactory _mailboxFactory) {
        FactoryLocator factoryLocator = getFactoryLocator(_mailboxFactory);
        registerFactories(factoryLocator);
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

        SListFactory.registerFactories(_factoryLocator);
        BListFactory.registerFactories(_factoryLocator);

        StringSMapFactory.registerFactories(_factoryLocator);
        LongSMapFactory.registerFactories(_factoryLocator);
        IntegerSMapFactory.registerFactories(_factoryLocator);

        StringBMapFactory.registerFactories(_factoryLocator);
        LongBMapFactory.registerFactories(_factoryLocator);
        IntegerBMapFactory.registerFactories(_factoryLocator);
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

    public static Factory getFactory(final FactoryLocator _factoryLocator, final String _factoryName) {
        if (_factoryLocator == null)
            throw new IllegalArgumentException("Unknown jid type: " + _factoryName);
        return _factoryLocator.getFactory(_factoryName);
    }

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _length The number of characters in the string.
     * @return The size in bytes.
     */
    public final static int stringLength(final int _length) {
        if (_length == -1)
            return INT_LENGTH;
        if (_length > -1)
            return INT_LENGTH + 2 * _length;
        throw new IllegalArgumentException("invalid string length: " + _length);
    }

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _s The string.
     * @return The size in bytes.
     */
    public final static int stringLength(final String _s) {
        if (_s == null)
            return INT_LENGTH;
        return stringLength(_s.length());
    }
}
