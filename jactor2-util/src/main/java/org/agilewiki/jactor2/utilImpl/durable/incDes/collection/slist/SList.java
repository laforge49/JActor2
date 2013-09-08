package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAList;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.CollectionImpl;

import java.util.ArrayList;

/**
 * Holds an ArrayList of JID actors, all of the same type.
 */
public class SList<ENTRY_TYPE extends JASerializable>
        extends CollectionImpl<ENTRY_TYPE>
        implements JAList<ENTRY_TYPE> {

    public int initialCapacity = 10;

    /**
     * IncDesImpl factory of the elements.
     */
    protected Factory entryFactory;

    /**
     * A list of JID actors.
     */
    protected ArrayList<ENTRY_TYPE> list;

    public AsyncRequest<Void> emptyReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                empty();
                processResponse(null);
            }
        };
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size()
            throws Exception {
        initializeList();
        return list.size();
    }

    /**
     * Returns the ith JID component.
     *
     * @param i The index of the element of interest.
     *          If negative, the index used is increased by the size of the collection,
     *          so that -1 returns the last element.
     * @return The ith JID component, or null if the index is out of range.
     */
    @Override
    public ENTRY_TYPE iGet(int i)
            throws Exception {
        initializeList();
        if (i < 0)
            i += list.size();
        if (i < 0 || i >= list.size())
            return null;
        return list.get(i);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() {
        return JAInteger.LENGTH * 2 + len;
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
        len = loadLen(readableBytes);
        list = null;
        readableBytes.skip(JAInteger.LENGTH + len);
    }

    /**
     * Returns the IncDesFactory for all the elements in the list.
     *
     * @return The IncDesFactory for of all the elements in the list.
     */
    protected Factory getEntryFactory() throws Exception {
        if (entryFactory == null)
            throw new IllegalStateException("entryFactory uninitialized");
        return entryFactory;
    }

    /**
     * Perform lazy initialization.
     */
    protected void initializeList()
            throws Exception {
        if (list != null)
            return;
        entryFactory = getEntryFactory();
        if (!isSerialized()) {
            list = new ArrayList<ENTRY_TYPE>();
            return;
        }
        ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        int count = readableBytes.readInt();
        list = new ArrayList<ENTRY_TYPE>(count);
        int i = 0;
        while (i < count) {
            ENTRY_TYPE elementJid = (ENTRY_TYPE) createSubordinate(entryFactory, this, readableBytes);
            list.add(elementJid);
            i += 1;
        }
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
        saveLen(appendableBytes);
        appendableBytes.writeInt(size());
        int i = 0;
        while (i < size()) {
            ((IncDesImpl) iGet(i).getDurable()).save(appendableBytes);
            i += 1;
        }
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public JASerializable resolvePathname(String pathname)
            throws Exception {
        initializeList();
        return super.resolvePathname(pathname);
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param i     The index of the desired element.
     * @param bytes Holds the serialized data.
     */
    @Override
    public void iSet(int i, byte[] bytes)
            throws Exception {
        initializeList();
        if (i < 0)
            i += list.size();
        if (i < 0 || i >= list.size())
            throw new IllegalArgumentException();
        JASerializable elementJid = createSubordinate(entryFactory, this, bytes);
        JASerializable oldElementJid = iGet(i);
        ((IncDesImpl) oldElementJid.getDurable()).setContainerJid(null);
        list.set(i, (ENTRY_TYPE) elementJid);
        change(elementJid.getDurable().getSerializedLength() -
                oldElementJid.getDurable().getSerializedLength());
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iAdd(_i, _bytes);
                processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i, byte[] bytes)
            throws Exception {
        initializeList();
        if (i < 0)
            i = size() + 1 + i;
        JASerializable jid = createSubordinate(entryFactory, this, bytes);
        int c = jid.getDurable().getSerializedLength();
        list.add(i, (ENTRY_TYPE) jid);
        change(c);
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iAdd(_i);
                processResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i)
            throws Exception {
        initializeList();
        if (i < 0)
            i = size() + 1 + i;
        JASerializable jid = createSubordinate(entryFactory, this);
        int c = jid.getDurable().getSerializedLength();
        list.add(i, (ENTRY_TYPE) jid);
        change(c);
    }

    @Override
    public void empty()
            throws Exception {
        int c = 0;
        int i = 0;
        int s = size();
        while (i < s) {
            JASerializable jid = iGet(i);
            ((IncDesImpl) jid.getDurable()).setContainerJid(null);
            c -= jid.getDurable().getSerializedLength();
            i += 1;
        }
        list.clear();
        change(c);
    }

    @Override
    public AsyncRequest<Void> iRemoveReq(final int _i) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                iRemove(_i);
                processResponse(null);
            }
        };
    }

    @Override
    public void iRemove(int i)
            throws Exception {
        int s = size();
        if (i < 0)
            i += s;
        if (i < 0 || i >= s)
            throw new IllegalArgumentException();
        JASerializable jid = (IncDesImpl) iGet(i);
        ((IncDesImpl) jid.getDurable()).setContainerJid(null);
        int c = -jid.getDurable().getSerializedLength();
        list.remove(i);
        change(c);
    }

    public void initialize(final MessageProcessor messageProcessor, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(messageProcessor, parent, factory);
    }
}
