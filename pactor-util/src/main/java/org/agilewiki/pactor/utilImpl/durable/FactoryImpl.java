package org.agilewiki.pactor.utilImpl.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.PASerializable;

/**
 * Creates a JLPCActor.
 */
abstract public class FactoryImpl implements Factory {

    /**
     * The jid type.
     */
    public final String name;
    private String factoryKey;

    @Override
    public String getName() {
        return name;
    }

    public void configure(final String _factoryKey) {
        factoryKey = _factoryKey;
    }

    @Override
    public String getFactoryKey() {
        return factoryKey;
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
    abstract protected PASerializable instantiateActor();

    @Override
    public PASerializable newSerializable(final Mailbox _mailbox) {
        return newSerializable(_mailbox, null);
    }

    /**
     * Create and configure an actor.
     *
     * @param _mailbox The mailbox of the new actor.
     * @param _parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public PASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent) {
        PASerializable a = instantiateActor();
        ((IncDesImpl) a.getDurable()).initialize(_mailbox, _parent, this);
        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Factory))
            return false;
        Factory af = (Factory) o;
        return factoryKey.equals(af.getFactoryKey());
    }
}
