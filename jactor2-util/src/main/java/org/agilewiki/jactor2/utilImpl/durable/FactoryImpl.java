package org.agilewiki.jactor2.utilImpl.durable;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

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
    abstract protected JASerializable instantiateBlade() throws Exception;

    @Override
    public JASerializable newSerializable(final Reactor _reactor) throws Exception {
        return newSerializable(_reactor, null);
    }

    /**
     * Create and configure an actor.
     *
     * @param _reactor The processing of the new actor.
     * @param _parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public JASerializable newSerializable(final Reactor _reactor, final Ancestor _parent) throws Exception {
        JASerializable a = instantiateBlade();
        ((IncDesImpl) a.getDurable()).initialize(_reactor, _parent, this);
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
