package org.agilewiki.jactor2.util.durable.block;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.incDes.Root;

/**
 * A wrapper for a Root to be read from or written to disk.
 * The header added to the serialized data contains the length of the serialized
 * data and, optionally, a timestamp and checksum.
 */
public interface Block {
    /**
     * Reset the block and assign the Root.
     *
     * @param root The RootImpl to be assigned.
     */
    public void setRootJid(Root root);

    /**
     * Serializes the header and the assigned Root.
     *
     * @return The bytes of the header and serialized Root.
     */
    public byte[] serialize()
            throws Exception;

    /**
     * The length of the header which prefaces the actual data on disk.
     *
     * @return The header length.
     */
    public int headerLength();

    /**
     * Returns the file name.
     *
     * @return The file name.
     */
    public String getFileName();

    /**
     * Assigns the file's name.
     */
    public void setFileName(String fileName);

    /**
     * Returns the file position.
     *
     * @return The file position.
     */
    public long getCurrentPosition();

    /**
     * Assigns the file's current position.
     */
    public void setCurrentPosition(long position);

    /**
     * Provides the raw header information read from disk.
     *
     * @param bytes The header bytes read from disk.
     * @return The length of the data following the header on disk.
     */
    public int setHeaderBytes(byte[] bytes);

    /**
     * Provides the data read from disk after the header.
     *
     * @param bytes The data following the header on disk.
     * @return True when the data is valid.
     */
    public boolean setRootBytes(byte[] bytes);

    /**
     * Get an existing Root.
     *
     * @return The Root.
     */
    public Root getRoot()
            throws Exception;

    /**
     * Return the Root, partially deserializing it as needed..
     *
     * @param mailbox The mailbox.
     * @param parent  The parent.
     * @return The Root, or null.
     */
    public Root getRoot(FactoryLocator factoryLocator, Mailbox mailbox, Ancestor parent)
            throws Exception;

    /**
     * Indicates the absence of a root object.
     *
     * @return True when a root jit is not present.
     */
    public boolean isEmpty();

    /**
     * Returns the timestamp.
     *
     * @return The timestamp.
     */
    public long getTimestamp();

    /**
     * Assigns the timestamp.
     *
     * @param timestamp The timestamp.
     */
    public void setTimestamp(long timestamp);
}
