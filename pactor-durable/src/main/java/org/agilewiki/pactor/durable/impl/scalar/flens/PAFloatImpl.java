package org.agilewiki.pactor.durable.impl.scalar.flens;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.Transport;
import org.agilewiki.pactor.durable.*;
import org.agilewiki.pactor.durable.impl.FactoryImpl;
import org.agilewiki.pautil.Ancestor;

/**
 * A JID actor that holds a float.
 */
public class PAFloatImpl
        extends FLenScalar<Float> implements PAFloat {

    public static void registerFactory(FactoryLocator factoryLocator)
            throws Exception {
        factoryLocator.registerFactory(new FactoryImpl(PAFloat.FACTORY_NAME) {
            @Override
            final protected PAFloatImpl instantiateActor()
                    throws Exception {
                return new PAFloatImpl();
            }
        });
    }

    private Request<Float> getFloatReq;

    @Override
    public Request<Float> getFloatReq() {
        return getFloatReq;
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Float newValue() {
        return new Float(0.F);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Float getValue() {
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        value = readableBytes.readFloat();
        return value;
    }

    @Override
    public Request<Void> setFloatReq(final Float v) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                setValue(v);
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
        return Util.FLOAT_LENGTH;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        appendableBytes.writeFloat(value);
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) throws Exception {
        super.initialize(mailbox, parent, factory);
        getFloatReq = new RequestBase<Float>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
