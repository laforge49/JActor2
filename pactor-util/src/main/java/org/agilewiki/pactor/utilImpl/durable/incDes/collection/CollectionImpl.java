package org.agilewiki.pactor.utilImpl.durable.incDes.collection;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.api.RequestBase;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.Collection;
import org.agilewiki.pactor.util.durable.incDes.PAInteger;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;
import org.agilewiki.pactor.utilImpl.durable.incDes.IncDesImpl;

/**
 * A collection of JID actors.
 */
abstract public class CollectionImpl<ENTRY_TYPE extends PASerializable>
        extends IncDesImpl
        implements Collection<ENTRY_TYPE> {

    /**
     * The size of the serialized data (exclusive of its length header).
     */
    protected int len;

    private Request<Integer> sizeReq;

    @Override
    public Request<Integer> sizeReq() {
        return sizeReq;
    }

    @Override
    public Request<ENTRY_TYPE> iGetReq(final int _i) {
        return new RequestBase<ENTRY_TYPE>(getMailbox()) {
            @Override
            public void processRequest(Transport<ENTRY_TYPE> _rp) throws Exception {
                _rp.processResponse(iGet(_i));
            }
        };
    }

    @Override
    public Request<Void> iSetReq(final int _i, final byte[] _bytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport _rp) throws Exception {
                iSet(_i, _bytes);
                _rp.processResponse(null);
            }
        };
    }

    /**
     * Skip over the length at the beginning of the serialized data.
     *
     * @param readableBytes Holds the serialized data.
     */
    protected void skipLen(ReadableBytes readableBytes) {
        readableBytes.skip(PAInteger.LENGTH);
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the serialized data (exclusive of its length header).
     */
    protected int loadLen(ReadableBytes readableBytes) {
        return readableBytes.readInt();
    }

    /**
     * Writes the size of the serialized data (exclusive of its length header).
     *
     * @param appendableBytes The object written to.
     */
    protected void saveLen(AppendableBytes appendableBytes) {
        appendableBytes.writeInt(len);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    @Override
    public void change(int lengthChange) {
        len += lengthChange;
        super.change(lengthChange);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public PASerializable resolvePathname(String pathname)
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
        if (n < 0 || n >= size())
            throw new IllegalArgumentException("pathname " + pathname);
        PASerializable jid = iGet(n);
        if (s == pathname.length())
            return jid;
        return jid.getDurable().resolvePathname(pathname.substring(s + 1));
    }

    public void initialize(final Mailbox mailbox, Ancestor parent, FactoryImpl factory)
            throws Exception {
        super.initialize(mailbox, parent, factory);
        sizeReq = new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport<Integer> _rp) throws Exception {
                _rp.processResponse(size());
            }
        };
    }
}
