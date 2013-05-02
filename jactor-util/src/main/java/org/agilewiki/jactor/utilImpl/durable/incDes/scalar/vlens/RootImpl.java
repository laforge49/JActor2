package org.agilewiki.jactor.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.durable.JASerializable;
import org.agilewiki.jactor.util.durable.incDes.JAString;
import org.agilewiki.jactor.util.durable.incDes.Root;
import org.agilewiki.jactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * The root IncDesImpl actor of a tree of IncDesImpl actors.
 * <p/>
 * The serialized form of RootImpl does NOT contain its length.
 * The load method simply grabs all the remaining data.
 */
public class RootImpl extends BoxImpl implements Root {

    public static void registerFactory(FactoryLocator _factoryLocator) {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(Root.FACTORY_NAME) {
            @Override
            final protected RootImpl instantiateActor() {
                return new RootImpl();
            }
        });
    }

    private JAString descriptor;

    @Override
    public String getDescriptor()
            throws Exception {
        return descriptor.getValue();
    }

    @Override
    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(mailbox, parent, factory);
        FactoryLocator factoryLocator = Durables.getFactoryLocator(getMailbox());
        descriptor = (JAString) Durables.newSerializable(JAString.FACTORY_NAME, mailbox);
        descriptor.setValue(((FactoryLocatorImpl) factoryLocator).getDescriptor());
    }

    /**
     * Throws an UnsupportedOperationException,
     * as a RootImpl does NOT have a container.
     *
     * @param containerJid The container, or null.
     */
    @Override
    public void setContainerJid(IncDesImpl containerJid) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the remaining bytes of serialized data.
     */
    @Override
    protected int loadLen(ReadableBytes readableBytes)
            throws Exception {
        descriptor.load(readableBytes);
        int l = readableBytes.remaining();
        if (l == 0)
            return -1;
        return l;
    }

    /**
     * There is no length, so there is nothing to skip over.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    protected void skipLen(ReadableBytes readableBytes)
            throws Exception {
        readableBytes.skip(descriptor.getSerializedLength());
    }

    /**
     * The length is not saved.
     *
     * @param appendableBytes The object written to.
     */
    @Override
    protected void saveLen(AppendableBytes appendableBytes)
            throws Exception {
        ((IncDesImpl) descriptor).save(appendableBytes);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength()
            throws Exception {
        if (len == -1)
            return descriptor.getSerializedLength();
        return descriptor.getSerializedLength() + len;
    }

    public JASerializable copy(Mailbox m)
            throws Exception {
        Mailbox mb = m;
        if (mb == null)
            mb = getMailbox();
        JASerializable jid = getFactory().newSerializable(mb, getParent());
        jid.getDurable().load(new ReadableBytes(getSerializedBytes(), 0));
        return jid;
    }
}
