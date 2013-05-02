package org.agilewiki.jactor.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.durable.incDes.PALong;
import org.agilewiki.jactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor.utilImpl.durable.ReadableBytes;

/**
 * A JID actor that holds a long.
 */
public class PALongImpl
        extends FLenScalar<Long> implements PALong {

    public static void registerFactory(FactoryLocator _factoryLocator) {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(PALong.FACTORY_NAME) {
            @Override
            final protected PALongImpl instantiateActor() {
                return new PALongImpl();
            }
        });
    }

    private Request<Long> getLongReq;

    @Override
    public Request<Long> getValueReq() {
        return getLongReq;
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Long newValue() {
        return new Long(0L);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Long getValue() {
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        value = readableBytes.readLong();
        return value;
    }

    @Override
    public Request<Void> setValueReq(final Long _v) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(_v);
                rp.processResponse(null);
            }
        };
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
        appendableBytes.writeLong(value);
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(mailbox, parent, factory);
        getLongReq = new RequestBase<Long>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
