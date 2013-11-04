package org.agilewiki.jactor2.util.durable.block;

import org.agilewiki.jactor2.util.durable.incDes.JALong;
import org.agilewiki.jactor2.util.durable.incDes.Root;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;

/**
 * A block with a length and a timestamp in the header.
 */
public class LTBlock extends LBlock {
    long timestamp;

    @Override
    public void setRootJid(final Root rootJid) {
        super.setRootJid(rootJid);
        timestamp = 0L;
    }

    @Override
    public int headerLength() {
        return super.headerLength() + JALong.LENGTH;
    }

    @Override
    protected void saveHeader(final AppendableBytes ab, final int l)
            throws Exception {
        if (timestamp == 0) {
            throw new IllegalStateException("timestamp not set");
        }
        super.saveHeader(ab, l);
        ab.writeLong(timestamp);
    }

    @Override
    public int setHeaderBytes(final byte[] bytes) {
        final int l = super.setHeaderBytes(bytes);
        timestamp = rb.readLong();
        return l;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
}
