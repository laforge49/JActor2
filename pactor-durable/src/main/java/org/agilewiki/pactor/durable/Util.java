package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pautil.Ancestor;

public class Util {

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

    public static FactoryLocator getFactoryLocator(final PASerializable _serializable) {
        return _serializable.getDurable().getFactory().getFactoryLocator();
    }

    public static PASerializable newSerializable(final PASerializable _serializable,
                                                 final String _factoryName)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_serializable),
                _factoryName,
                _serializable.getMailbox().getMailboxFactory(),
                null);
    }

    public static PASerializable newSerializable(final PASerializable _serializable,
                                                 final String _factoryName,
                                                 final Ancestor _parent)
            throws Exception {
        return newSerializable(
                getFactoryLocator(_serializable),
                _factoryName,
                _serializable.getMailbox().getMailboxFactory(),
                _parent);
    }

    public static PASerializable newSerializable(final PASerializable _serializable,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox)
            throws Exception {
        return newSerializable(getFactoryLocator(_serializable), _factoryName, _mailbox, null);
    }

    public static PASerializable newSerializable(final PASerializable _serializable,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return newSerializable(getFactoryLocator(_serializable), _factoryName, _mailbox, _parent);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final Mailbox _mailbox,
                                                 final Ancestor _parent)
            throws Exception {
        return _factoryLocator.newSerializable(_factoryName, _mailbox, _parent);
    }

    public static PASerializable newSerializable(final FactoryLocator _factoryLocator,
                                                 final String _factoryName,
                                                 final MailboxFactory _mailboxFactory,
                                                 final Ancestor _parent)
            throws Exception {
        return _factoryLocator.newSerializable(_factoryName, _mailboxFactory.createMailbox(), _parent);
    }

    public static Factory getActorFactory(final PASerializable _serializable, final String _factoryName)
            throws Exception {
        return getActorFactory(getFactoryLocator(_serializable), _factoryName);
    }

    public static Factory getActorFactory(final FactoryLocator _factoryLocator, final String _factoryName)
            throws Exception {
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
