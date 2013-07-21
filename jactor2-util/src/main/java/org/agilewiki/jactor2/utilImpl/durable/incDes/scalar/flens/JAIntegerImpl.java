package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Request;
import org.agilewiki.jactor2.api.Transport;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

/**
 * A JID actor that holds an integer.
 */
public class JAIntegerImpl
        extends FLenScalar<Integer> implements JAInteger {

    public static void registerFactory(FactoryLocator _factoryLocator) throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(JAInteger.FACTORY_NAME) {
            @Override
            final protected JAIntegerImpl instantiateActor() {
                return new JAIntegerImpl();
            }
        });
    }

    private Request<Integer> getIntegerReq;

    @Override
    public Request<Integer> getValueReq() {
        return getIntegerReq;
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Integer newValue() {
        return new Integer(0);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Integer getValue() {
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        value = readableBytes.readInt();
        return value;
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        appendableBytes.writeInt(value);
    }

    @Override
    public Request<Void> setValueReq(final Integer v) {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> rp) throws Exception {
                setValue(v);
                rp.processResponse(null);
            }
        };
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(mailbox, parent, factory);
        getIntegerReq = new Request<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
