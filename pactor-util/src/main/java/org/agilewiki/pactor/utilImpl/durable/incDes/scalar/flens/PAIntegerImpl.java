package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.PAInteger;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.FactoryLocatorImpl;

/**
 * A JID actor that holds an integer.
 */
public class PAIntegerImpl
        extends FLenScalar<Integer> implements PAInteger {

    public static void registerFactory(FactoryLocator _factoryLocator) {
        ((FactoryLocatorImpl)_factoryLocator).registerFactory(new FactoryImpl(PAInteger.FACTORY_NAME) {
            @Override
            final protected PAIntegerImpl instantiateActor() {
                return new PAIntegerImpl();
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
        return new RequestBase<Void>(getMailbox()) {
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
        getIntegerReq = new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
