package org.agilewiki.pactor.util.durable.block;

import org.agilewiki.pactor.util.durable.AppendableBytes;
import org.agilewiki.pactor.util.durable.incDes.Root;
import org.agilewiki.pactor.utilImpl.durable.DurablesImpl;

/**
 * A block with a length and a timestamp in the header.
 */
public class LTBlock extends LBlock {
    long timestamp;

    /**
     * Reset the block and assign the RootImpl.
     *
     * @param rootJid The RootImpl to be assigned.
     */
    @Override
    public void setRootJid(Root rootJid) {
        super.setRootJid(rootJid);
        timestamp = 0L;
    }

    /**
     * The length of the header which prefaces the actual data on disk.
     *
     * @return The header length.
     */
    @Override
    public int headerLength() {
        return super.headerLength() + DurablesImpl.LONG_LENGTH;
    }

    /**
     * Provides the raw header information to be written to disk.
     *
     * @param ab Append the data to this.
     * @param l  The length of the data.
     */
    @Override
    protected void saveHeader(AppendableBytes ab, int l)
            throws Exception {
        if (timestamp == 0)
            throw new IllegalStateException("timestamp not set");
        super.saveHeader(ab, l);
        ab.writeLong(timestamp);
    }

    /**
     * Provides the raw header information read from disk.
     *
     * @param bytes The header bytes read from disk.
     * @return The length of the data following the header on disk.
     */
    @Override
    public int setHeaderBytes(byte[] bytes) {
        int l = super.setHeaderBytes(bytes);
        timestamp = rb.readLong();
        return l;
    }

    /**
     * Returns the timestamp.
     *
     * @return The timestamp.
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Assigns the timestamp.
     *
     * @param timestamp The timestamp.
     */
    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
