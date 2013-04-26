package org.agilewiki.pactor.durable.impl;

import org.agilewiki.pactor.durable.*;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pautil.Ancestor;

/**
 * Creates a JLPCActor.
 */
abstract public class FactoryImpl implements Factory {
    private String factoryKey;

    /**
     * The jid type.
     */
    public final String name;
    private FactoryLocator factoryLocator;

    @Override
    public String getName() {
        return name;
    }

    public void configure(final FactoryLocator _factoryLocator) {
        factoryLocator = _factoryLocator;
    }

    public FactoryLocator getFactoryLocator() {
        return factoryLocator;
    }

    public String getFactoryKey() {
        if (factoryKey == null)
            factoryKey = name + "|" + getLocatorKey();
        return factoryKey;
    }

    public String getLocatorKey() {
        return factoryLocator.getLocatorKey();
    }

    public String getLocation() {
        return factoryLocator.getLocation();
    }

    /**
     * Create an FactoryImpl.
     *
     * @param _name The jid type.
     */
    public FactoryImpl(final String _name) {
        name = _name;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    abstract protected PASerializable instantiateActor()
            throws Exception;

    /**
     * Create and configure an actor.
     *
     * @param _mailbox The mailbox of the new actor.
     * @param _parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public PASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent)
            throws Exception {
        PASerializable a = instantiateActor();
        ((IncDesImpl) a.getDurable()).initialize(_mailbox, _parent, this);
        return a;
    }
}
