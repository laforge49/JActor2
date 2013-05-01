package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.utilImpl.durable.ReadableBytes;

public interface IncDes extends PASerializable, Actor, Ancestor {

    public static final String FACTORY_NAME = "incdes";

    Request<Integer> getSerializedLengthReq();

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    int getSerializedLength()
            throws Exception;

    Request<byte[]> getSerializedBytesReq();

    byte[] getSerializedBytes()
            throws Exception;

    Request<Integer> getSerializedBytesReq(byte[] bytes, int offset);

    int save(byte[] bytes, int offset)
            throws Exception;

    /**
     * Load the serialized data into the JID.
     *
     * @param _readableBytes Holds the serialized data.
     */
    void load(final ReadableBytes _readableBytes)
            throws Exception;

    void load(final byte[] _bytes) throws Exception;

    int load(final byte[] _bytes, final int _offset, final int _length)
            throws Exception;

    Request<PASerializable> resolvePathnameReq(final String _pathname);

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param _pathname A JID pathname.
     * @return A JID actor or null.
     */
    PASerializable resolvePathname(final String _pathname)
            throws Exception;

    /**
     * Returns the factory.
     *
     * @return The factory, or null.
     */
    Factory getFactory();

    /**
     * Returns the jid type.
     *
     * @return The jid type, or null.
     */
    String getType();

    Request<PASerializable> copyReq(final Mailbox _m);

    PASerializable copy(final Mailbox m) throws Exception;

    Request<Boolean> isEqualReq(final PASerializable _jidA);
}
