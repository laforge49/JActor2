package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.AppendableBytes;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.PABoolean;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

/**
 * A JID actor that holds a boolean.
 */
public class PABooleanImpl
        extends FLenScalar<Boolean> implements PABoolean {

    /**
     * Size of a boolean in bytes.
     */
    public final static int BOOLEAN_LENGTH = 1;

    public static void registerFactory(FactoryLocator factoryLocator) {
        factoryLocator.registerFactory(new FactoryImpl(PABoolean.FACTORY_NAME) {
            @Override
            final protected PABooleanImpl instantiateActor() {
                return new PABooleanImpl();
            }
        });
    }

    private Request<Boolean> getBooleanReq;

    @Override
    public Request<Boolean> getValueReq() {
        return getBooleanReq;
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Boolean newValue() {
        return new Boolean(false);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Boolean getValue() {
        if (value != null)
            return value;
        ReadableBytes readableBytes = readable();
        value = readableBytes.readBoolean();
        return value;
    }

    @Override
    public Request<Void> setValueReq(final Boolean v) {
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
        return BOOLEAN_LENGTH;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes) {
        appendableBytes.writeBoolean(((Boolean) value).booleanValue());
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory) {
        super.initialize(mailbox, parent, factory);
        getBooleanReq = new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getValue());
            }
        };
    }
}
