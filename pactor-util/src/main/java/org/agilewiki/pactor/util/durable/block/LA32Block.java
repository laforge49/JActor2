package org.agilewiki.pactor.util.durable.block;

import org.agilewiki.pactor.util.durable.AppendableBytes;
import org.agilewiki.pactor.util.durable.incDes.PALong;

import java.util.zip.Adler32;

/**
 * A block with a length and Adler32 checksum in the header.
 */
public class LA32Block extends LBlock {
    Adler32 a32 = new Adler32();
    long checksum;

    /**
     * The length of the header which prefaces the actual data on disk.
     *
     * @return The header length.
     */
    @Override
    public int headerLength() {
        return super.headerLength() + PALong.LENGTH;
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
        super.saveHeader(ab, l);
        a32.reset();
        a32.update(blockBytes, headerLength(), blockBytes.length - headerLength());
        ab.writeLong(a32.getValue());
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
        checksum = rb.readLong();
        return l;
    }

    /**
     * Provides the data read from disk after the header.
     *
     * @param bytesRead The data following the header on disk.
     * @return True when the data is valid.
     */
    @Override
    public boolean setRootJidBytes(byte[] bytesRead) {
        if (!super.setRootJidBytes(bytesRead))
            return false;
        int i = 0;
        while (i < bytesRead.length) {
            i += 1;
        }
        a32.reset();
        a32.update(bytesRead);
        boolean match = checksum == a32.getValue();
        if (match)
            return true;
        System.out.println("bad checksum");
        rootJidBytes = null;
        return false;
    }
}
