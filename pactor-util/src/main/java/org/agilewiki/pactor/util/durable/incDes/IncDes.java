package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.api.Actor;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.Request;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.durable.AppendableBytes;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.util.durable.ReadableBytes;

public interface IncDes extends PASerializable, Actor, Ancestor {

    public static final String FACTORY_NAME = "incdes";

    Request<Integer> getSerializedLengthReq();

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    int getSerializedLength();

    Request<byte[]> getSerializedBytesReq();

    byte[] getSerializedBytes();

    Request<Void> saveReq(final AppendableBytes _appendableBytes);

    /**
     * Saves the persistent data in a byte array.
     *
     * @param _appendableBytes Holds the byte array and offset.
     */
    void save(final AppendableBytes _appendableBytes);

    /**
     * Load the serialized data into the JID.
     *
     * @param _readableBytes Holds the serialized data.
     */
    void load(final ReadableBytes _readableBytes);

    Request<PASerializable> resolvePathnameReq(final String _pathname);

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param _pathname A JID pathname.
     * @return A JID actor or null.
     */
    PASerializable resolvePathname(final String _pathname);

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

    PASerializable copy(final Mailbox m);

    Request<Boolean> isEqualReq(final PASerializable _jidA);
}
