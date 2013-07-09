package org.agilewiki.jactor2.utilImpl.durable.app;

import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.app.Durable;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * A base class for applications, DurableImpl provides a durable tuple without an external interface.
 */
public class DurableImpl extends IncDesImpl implements Durable {
    /**
     * The size of the serialized data (exclusive of its length header).
     */
    private int _len;

    /**
     * An array of jid factories, one for each element in the tuple.
     */
    public Factory[] tupleFactories;

    /**
     * A tuple of actors.
     */
    protected JASerializable[] tuple;

    /**
     * Returns the element factories.
     *
     * @return An array of element factories.
     */
    protected Factory[] getTupleFactories() {
        if (tupleFactories != null)
            return tupleFactories;
        throw new IllegalStateException("tupleFactories is null");
    }

    @Override
    public void _iSetBytes(int i, byte[] bytes)
            throws Exception {
        _initialize();
        JASerializable elementJid = createSubordinate(tupleFactories[i], bytes);
        JASerializable oldElementJid = _iGet(i);
        ((IncDesImpl) oldElementJid.getDurable()).setContainerJid(null);
        tuple[i] = elementJid;
        change(elementJid.getDurable().getSerializedLength() -
                oldElementJid.getDurable().getSerializedLength());
    }

    @Override
    public int _size() {
        return getTupleFactories().length;
    }

    @Override
    public JASerializable _iGet(int i)
            throws Exception {
        _initialize();
        if (i < 0)
            i += _size();
        if (i < 0 || i >= _size())
            return null;
        return tuple[i];
    }

    @Override
    public JASerializable _resolvePathname(String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        int s = pathname.indexOf("/");
        if (s == -1)
            s = pathname.length();
        if (s == 0)
            throw new IllegalArgumentException("pathname " + pathname);
        String ns = pathname.substring(0, s);
        int n = 0;
        try {
            n = Integer.parseInt(ns);
        } catch (Exception ex) {
            throw new IllegalArgumentException("pathname " + pathname);
        }
        if (n < 0 || n >= _size())
            throw new IllegalArgumentException("pathname " + pathname);
        JASerializable jid = _iGet(n);
        if (s == pathname.length())
            return jid;
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    /**
     * Perform lazy initialization.
     */
    private void _initialize()
            throws Exception {
        if (tuple != null)
            return;
        tupleFactories = getTupleFactories();
        ReadableBytes readableBytes = null;
        if (isSerialized()) {
            readableBytes = readable();
            _skipLen(readableBytes);
        }
        tuple = new JASerializable[_size()];
        int i = 0;
        _len = 0;
        while (i < _size()) {
            JASerializable elementJid = createSubordinate(tupleFactories[i], readableBytes);
            _len += elementJid.getDurable().getSerializedLength();
            tuple[i] = elementJid;
            i += 1;
        }
    }

    /**
     * Skip over the length at the beginning of the serialized data.
     *
     * @param readableBytes Holds the serialized data.
     */
    private void _skipLen(ReadableBytes readableBytes) {
        readableBytes.skip(JAInteger.LENGTH);
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the serialized data (exclusive of its length header).
     */
    private int _loadLen(ReadableBytes readableBytes) {
        return readableBytes.readInt();
    }

    /**
     * Writes the size of the serialized data (exclusive of its length header).
     *
     * @param appendableBytes The object written to.
     */
    private void _saveLen(AppendableBytes appendableBytes) {
        appendableBytes.writeInt(_len);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength()
            throws Exception {
        _initialize();
        return JAInteger.LENGTH + _len;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
        _saveLen(appendableBytes);
        int i = 0;
        while (i < _size()) {
            ((IncDesImpl) _iGet(i).getDurable()).save(appendableBytes);
            i += 1;
        }
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(ReadableBytes readableBytes)
            throws Exception {
        super.load(readableBytes);
        _len = _loadLen(readableBytes);
        tuple = null;
        readableBytes.skip(_len);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    @Override
    public void change(int lengthChange) {
        _len += lengthChange;
        super.change(lengthChange);
    }
}
