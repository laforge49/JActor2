package org.agilewiki.pactor.utilImpl.durable.incDes;

import org.agilewiki.pactor.api.*;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.AncestorBase;
import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.Factory;
import org.agilewiki.pactor.util.durable.PASerializable;
import org.agilewiki.pactor.util.durable.ReadableBytes;
import org.agilewiki.pactor.util.durable.incDes.IncDes;
import org.agilewiki.pactor.utilImpl.durable.AppendableBytes;
import org.agilewiki.pactor.utilImpl.durable.FactoryImpl;

import java.util.Arrays;

/**
 * Base class for Incremental Deserialization Actors.
 */
public class IncDesImpl extends AncestorBase implements IncDes {
    /**
     * The actor's mailbox.
     */
    private Mailbox mailbox;

    /**
     * The factory, or null.
     */
    private FactoryImpl factory;

    /**
     * The JID actor which holds this actor.
     */
    private IncDesImpl containerJid;

    /**
     * Holds the serialized data.
     */
    protected byte[] serializedBytes;

    /**
     * The start of the serialized data.
     */
    protected int serializedOffset;

    private Request<byte[]> getSerializedBytesReq;
    private Request<Integer> getSerializedLengthReq;

    @Override
    public IncDes getDurable() {
        return this;
    }

    @Override
    public Request<byte[]> getSerializedBytesReq() {
        return getSerializedBytesReq;
    }

    @Override
    public Request<Integer> getSerializedLengthReq() {
        return getSerializedLengthReq;
    }

    final public PASerializable createSubordinate(Factory factory)
            throws Exception {
        return createSubordinate(factory, getParent());
    }

    final public PASerializable createSubordinate(String actorType)
            throws Exception {
        return createSubordinate(actorType, getParent());
    }

    final public PASerializable createSubordinate(Factory factory, Ancestor parent)
            throws Exception {
        PASerializable jid = factory.newSerializable(getMailbox(), parent);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public PASerializable createSubordinate(String actorType, Ancestor parent)
            throws Exception {
        PASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(mailbox), actorType, getMailbox(), parent);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public PASerializable createSubordinate(Factory factory, byte[] bytes)
            throws Exception {
        return createSubordinate(factory, getParent(), bytes);
    }

    final public PASerializable createSubordinate(String actorType, byte[] bytes)
            throws Exception {
        return createSubordinate(actorType, getParent(), bytes);
    }

    final public PASerializable createSubordinate(Factory factory, Ancestor parent, byte[] bytes)
            throws Exception {
        if (bytes == null)
            return createSubordinate(factory, parent);
        PASerializable jid = factory.newSerializable(getMailbox(), parent);
        ((IncDesImpl) jid.getDurable()).load(new ReadableBytes(bytes, 0));
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public PASerializable createSubordinate(String actorType, Ancestor parent, byte[] bytes)
            throws Exception {
        if (bytes == null)
            return createSubordinate(actorType, parent);
        PASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(mailbox), actorType, getMailbox(), parent);
        ((IncDesImpl) jid.getDurable()).load(new ReadableBytes(bytes, 0));
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public PASerializable createSubordinate(Factory factory, ReadableBytes readableBytes)
            throws Exception {
        return createSubordinate(factory, getParent(), readableBytes);
    }

    final public PASerializable createSubordinate(String actorType, ReadableBytes readableBytes)
            throws Exception {
        return createSubordinate(actorType, getParent(), readableBytes);
    }

    final public PASerializable createSubordinate(Factory factory, Ancestor parent, ReadableBytes readableBytes)
            throws Exception {
        PASerializable jid = factory.newSerializable(getMailbox(), parent);
        if (readableBytes != null)
            ((IncDesImpl) jid.getDurable()).load(readableBytes);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    final public PASerializable createSubordinate(String actorType, Ancestor parent, ReadableBytes readableBytes)
            throws Exception {
        PASerializable jid = Durables.newSerializable(Durables.getFactoryLocator(mailbox), actorType, getMailbox(), parent);
        if (readableBytes != null)
            ((IncDesImpl) jid.getDurable()).load(readableBytes);
        ((IncDesImpl) jid.getDurable()).setContainerJid(this);
        return jid;
    }

    /**
     * Returns a readable form of the serialized data.
     *
     * @return A ReadableBytes wrapper of the serialized data.
     */
    final protected ReadableBytes readable() {
        return new ReadableBytes(serializedBytes, serializedOffset);
    }

    /**
     * Notification that the persistent data has changed.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    protected void changed(int lengthChange) {
        serializedBytes = null;
        serializedOffset = -1;
        if (containerJid == null)
            return;
        containerJid.change(lengthChange);
    }

    /**
     * Process a change in the persistent data.
     *
     * @param lengthChange The change in the size of the serialized data.
     */
    public void change(int lengthChange) {
        changed(lengthChange);
    }

    /**
     * Assign the container.
     *
     * @param containerJid The container, or null.
     */
    public void setContainerJid(IncDesImpl containerJid) {
        this.containerJid = containerJid;
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength()
            throws Exception {
        return 0;
    }

    /**
     * Returns true when the persistent data is already serialized.
     *
     * @return True when the persistent data is already serialized.
     */
    final protected boolean isSerialized() {
        return serializedBytes != null;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    protected void serialize(AppendableBytes appendableBytes)
            throws Exception {
    }

    /**
     * Saves the persistent data in a byte array.
     *
     * @param appendableBytes Holds the byte array and offset.
     */
    public void save(final AppendableBytes appendableBytes)
            throws Exception {
        if (isSerialized()) {
            byte[] bs = appendableBytes.getBytes();
            int off = appendableBytes.getOffset();
            appendableBytes.writeBytes(serializedBytes, serializedOffset, getSerializedLength());
            serializedBytes = bs;
            serializedOffset = off;
        } else {
            serializedBytes = appendableBytes.getBytes();
            serializedOffset = appendableBytes.getOffset();
            serialize(appendableBytes);
        }
        if (serializedOffset + getSerializedLength() != appendableBytes.getOffset()) {
            System.err.println("\n" + getClass().getName());
            System.err.println("" + serializedOffset +
                    " + " + getSerializedLength() + " != " + appendableBytes.getOffset());
            throw new IllegalStateException();
        }
    }

    final public Request<Void> saveReq(final AppendableBytes appendableBytes) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                save(appendableBytes);
                rp.processResponse(null);
            }
        };
    }

    /**
     * Returns a byte array holding the serialized persistent data.
     *
     * @return The byte array holding the serialized persistent data.
     */
    public final byte[] getSerializedBytes()
            throws Exception {
        byte[] bs = new byte[getSerializedLength()];
        AppendableBytes appendableBytes = new AppendableBytes(bs, 0);
        save(appendableBytes);
        return bs;
    }

    @Override
    final public Request<Integer> getSerializedBytesReq(final byte[] bytes, final int offset) {
        return new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getSerializedBytes(bytes, offset));
            }
        };
    }

    @Override
    public final int getSerializedBytes(byte[] bytes, int offset)
            throws Exception {
        AppendableBytes appendableBytes = new AppendableBytes(bytes, offset);
        save(appendableBytes);
        return appendableBytes.getOffset();
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(ReadableBytes readableBytes)
            throws Exception {
        serializedBytes = readableBytes.getBytes();
        serializedOffset = readableBytes.getOffset();
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public PASerializable resolvePathname(final String pathname)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Request<PASerializable> resolvePathnameReq(final String pathname) {
        return new RequestBase<PASerializable>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(resolvePathname(pathname));
            }
        };
    }

    /**
     * Returns a copy of the actor.
     *
     * @param m The mailbox.
     * @return a copy of the actor.
     */
    @Override
    public PASerializable copy(final Mailbox m)
            throws Exception {
        Mailbox mb = m;
        if (mb == null)
            mb = getMailbox();
        PASerializable serializable = getFactory().newSerializable(mb, getParent());
        IncDesImpl jid = (IncDesImpl) serializable.getDurable();
        jid.load(new ReadableBytes(getSerializedBytes(), 0));
        return serializable;
    }

    public final Request<PASerializable> copyReq(final Mailbox m) {
        return new RequestBase<PASerializable>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(copy(m));
            }
        };
    }

    public final Request<Boolean> isEqualReq(final PASerializable jidA) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final Transport rp) throws Exception {
                getSerializedLengthReq.send(getMailbox(), new ResponseProcessor<Integer>() {
                    @Override
                    public void processResponse(Integer response) throws Exception {
                        if (response.intValue() != getSerializedLength()) {
                            rp.processResponse(false);
                            return;
                        }
                        getSerializedBytesReq.send(getMailbox(), new ResponseProcessor<byte[]>() {
                            @Override
                            public void processResponse(byte[] response) throws Exception {
                                boolean eq = Arrays.equals(response, getSerializedBytes());
                                rp.processResponse(eq);
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Returns the factory.
     *
     * @return The factory, or null.
     */
    @Override
    final public FactoryImpl getFactory() {
        return factory;
    }

    /**
     * Returns the jid type.
     *
     * @return The jid type, or null.
     */
    @Override
    final public String getType() {
        if (factory == null)
            return null;
        return factory.name;
    }

    /**
     * Initialize a LiteActor
     *
     * @param _mailbox A mailbox which may be shared with other actors.
     * @param _parent  The parent actor.
     * @param _factory The factory.
     */
    public void initialize(final Mailbox _mailbox, final Ancestor _parent, final FactoryImpl _factory)
            throws Exception {
        super.initialize(_parent);
        mailbox = _mailbox;
        factory = _factory;

        getSerializedLengthReq = new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getSerializedLength());
            }
        };

        getSerializedBytesReq = new RequestBase<byte[]>(getMailbox()) {
            @Override
            public void processRequest(Transport rp) throws Exception {
                rp.processResponse(getSerializedBytes());
            }
        };
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
}
