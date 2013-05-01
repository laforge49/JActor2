package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.AppendableBytes;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.PADouble;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

/**
 * A JID actor that holds a double.
 */
public class PADoubleImpl
        extends FLenScalar<Double> implements PADouble {

    public static void registerFactory(FactoryLocator factoryLocator) {
        factoryLocator.registerFactory(new FactoryImpl(PADouble.FACTORY_NAME) {
            @Override
            final protected PADoubleImpl instantiateActor() {
                return new PADoubleImpl();
            }
        });
    }

    private Request<Double> getDoubleReq;

    @Override
    public Request<Double> getValueReq() {
        return getDoubleReq;
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Double newValue() {
        return new Double(0.D);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Double getValue() {
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        value = readableBytes.readDouble();
        return value;
    }

    @Override
    public Request<Void> setValueReq(final Double v) {
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
        return LENGTH;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        appendableBytes.writeDouble(value);
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        getDoubleReq = new RequestBase<Double>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
