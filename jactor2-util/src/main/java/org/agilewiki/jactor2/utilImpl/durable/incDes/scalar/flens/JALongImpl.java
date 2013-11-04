package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JALong;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

/**
 * A JID actor that holds a long.
 */
public class JALongImpl extends FLenScalar<Long> implements JALong {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                JALong.FACTORY_NAME) {
            @Override
            final protected JALongImpl instantiateBlade() {
                return new JALongImpl();
            }
        });
    }

    @Override
    public AsyncRequest<Long> getValueReq() {
        return new AsyncBladeRequest<Long>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse(getValue());
            }
        };
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
        if (value != null) {
            return value;
        }
        final ReadableBytes readableBytes = readable();
        value = readableBytes.readLong();
        return value;
    }

    @Override
    public AsyncRequest<Void> setValueReq(final Long _v) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                setValue(_v);
                processAsyncResponse(null);
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
    protected void serialize(final AppendableBytes appendableBytes) {
        appendableBytes.writeLong(value);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
