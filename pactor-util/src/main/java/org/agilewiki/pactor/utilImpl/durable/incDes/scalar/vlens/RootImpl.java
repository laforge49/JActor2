package org.agilewiki.pactor.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.PAString;
import org.agilewiki.pactor.util.durable.incDes.Root;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * The root IncDesImpl actor of a tree of IncDesImpl actors.
 * <p/>
 * The serialized form of RootImpl does NOT contain its length.
 * The load method simply grabs all the remaining data.
 */
public class RootImpl extends BoxImpl implements Root {

    public static void registerFactory(FactoryLocator factoryLocator) {
        factoryLocator.registerFactory(new FactoryImpl(Root.FACTORY_NAME) {
            @Override
            final protected RootImpl instantiateActor() {
                return new RootImpl();
            }
        });
    }

    private PAString descriptor;

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
        descriptor = (PAString) Durables.newSerializable(PAString.FACTORY_NAME, mailbox);
        descriptor.setValue(factoryLocator.getDescriptor());
    }

    /**
     * Save the serialized data into a byte array.
     *
     * @param bytes  Where the serialized data is to be saved.
     * @param offset Location of the serialized data.
     * @return Updated offset.
     */
    public int save(byte[] bytes, int offset)
            throws Exception {
        AppendableBytes appendableBytes = new AppendableBytes(bytes, offset);
        save(appendableBytes);
        return appendableBytes.getOffset();
    }

    /**
     * Load the serialized data into the RootImpl.
     *
     * @param bytes  A mutable array holding the serialized data.
     * @param offset Position of the serialized data.
     * @param length Length of the serialized data
     * @return The updated offset.
     */
    public int load(byte[] bytes, int offset, int length)
            throws Exception {
        byte[] bs = new byte[length];
        System.arraycopy(bytes, offset, bs, 0, length);
        load(bs);
        return offset + length;
    }

    /**
     * Assigns the serialized data to the RootImpl.
     *
     * @param bytes Holds the immutable serialized data.
     *              (And nothing else.)
     */
    public void load(byte[] bytes)
            throws Exception {
        ReadableBytes rb = new ReadableBytes(bytes, 0);
        load(rb);
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

    public PASerializable copy(Mailbox m)
            throws Exception {
        Mailbox mb = m;
        if (mb == null)
            mb = getMailbox();
        PASerializable jid = getFactory().newSerializable(mb, getParent());
        jid.getDurable().load(new ReadableBytes(getSerializedBytes(), 0));
        return jid;
    }
}
