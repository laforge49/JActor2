package org.agilewiki.jactor2.util.durable.block;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.Root;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.RootImpl;

/**
 * A block with a length in the header.
 * --A minimal block implementation.
 */
public class LBlock implements Block {
    private String fileName;
    private long currentPosition;
    protected ReadableBytes rb;
    int l;
    protected byte[] blockBytes;
    protected byte[] rootJidBytes;
    private Root rootJid;

    @Override
    public void setRootJid(final Root rootJid) {
        blockBytes = null;
        rootJidBytes = null;
        this.rootJid = rootJid;
    }

    @Override
    public byte[] serialize() throws Exception {
        if (blockBytes != null) {
            return blockBytes;
        }
        l = rootJid.getSerializedLength();
        blockBytes = new byte[headerLength() + l];
        AppendableBytes ab = new AppendableBytes(blockBytes, headerLength());
        ((RootImpl) rootJid).save(ab);
        ab = new AppendableBytes(blockBytes, 0);
        saveHeader(ab, l);
        return blockBytes;
    }

    /**
     * Provides the raw header information to be written to disk.
     *
     * @param ab Append the data to this.
     * @param l  The length of the data.
     */
    protected void saveHeader(final AppendableBytes ab, final int l)
            throws Exception {
        ab.writeInt(l);
    }

    @Override
    public int headerLength() {
        return JAInteger.LENGTH;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public long getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(final long position) {
        currentPosition = position;
    }

    @Override
    public int setHeaderBytes(final byte[] bytes) {
        rb = new ReadableBytes(bytes, 0);
        l = rb.readInt();
        return l;
    }

    @Override
    public boolean setRootBytes(final byte[] rootJidBytes) {
        if (l != rootJidBytes.length) {
            System.out.println("wrong length");
            return false;
        }
        this.rootJidBytes = rootJidBytes;
        return true;
    }

    @Override
    public Root getRoot() throws Exception {
        if (rootJid == null) {
            throw new IllegalStateException("there is no RootImpl");
        }
        return rootJid;
    }

    @Override
    public Root getRoot(final FactoryLocator factoryLocator,
            final Reactor reactor, final Ancestor parent) throws Exception {
        if (rootJid != null) {
            return rootJid;
        }
        rb = null;
        if (rootJidBytes == null) {
            return null;
        }
        rootJid = (Root) Durables.newSerializable(factoryLocator,
                Root.FACTORY_NAME, reactor, parent);
        ((RootImpl) rootJid).load(new ReadableBytes(rootJidBytes, 0));
        return rootJid;
    }

    @Override
    public boolean isEmpty() {
        return (rootJid == null) && (rootJidBytes == null);
    }

    @Override
    public long getTimestamp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimestamp(final long timestamp) {
        throw new UnsupportedOperationException();
    }
}
