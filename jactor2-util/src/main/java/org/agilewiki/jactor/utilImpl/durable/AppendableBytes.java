package org.agilewiki.jactor.utilImpl.durable;

/**
 * <p>
 * A mutable wrapper for an array of bytes.
 * </p>
 * <p>
 * Reads and writes read from and write to the wrapped byte array
 * while advancing an internal offset.
 * </p>
 */
final public class AppendableBytes {
    /**
     * The wrapped immutable array of bytes.
     */
    protected byte[] bytes;

    /**
     * A mutable offset into the array of bytes.
     */
    protected int offset;

    /**
     * Create AppendableBytes.
     *
     * @param bytes  The bytes to be wrapped.
     * @param offset An offset.
     */
    public AppendableBytes(byte[] bytes, int offset) {
        this.bytes = bytes;
        this.offset = offset;
    }

    /**
     * Create AppendableBytes.
     *
     * @param size The size of the byte array.
     */
    public AppendableBytes(int size) {
        this(new byte[size], 0);
    }

    /**
     * Returns the offset.
     *
     * @return The offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the bytes.
     *
     * @return The byte array.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Write selected bytes.
     *
     * @param readableBytes The source of the bytes to be written.
     * @param length        The number of bytes to be written.
     */
    public void writeReadableBytes(ReadableBytes readableBytes, int length) {
        readableBytes.readBytes(bytes, offset, length);
        offset += length;
    }

    /**
     * Write a byte.
     *
     * @param b The byte to be written.
     */
    public void writeByte(byte b) {
        bytes[offset] = b;
        offset += 1;
    }

    /**
     * Write a boolean.
     *
     * @param b The boolean to be written.
     */
    public void writeBoolean(boolean b) {
        if (b) writeByte((byte) 1);
        else writeByte((byte) 0);
    }

    /**
     * Write an array of bytes.
     *
     * @param ba The bytes to be written.
     */
    public void writeBytes(byte[] ba) {
        if (ba.length == 0) return;
        System.arraycopy(ba, 0, bytes, offset, ba.length);
        offset += ba.length;
    }

    /**
     * Write part of an array of bytes.
     *
     * @param ba  The array containing the bytes to be written.
     * @param off The offset to the bytes to be written.
     * @param len The number of bytes to be written.
     */
    public void writeBytes(byte[] ba, int off, int len) {
        if (len == 0) return;
        System.arraycopy(ba, off, bytes, offset, len);
        offset += len;
    }

    /**
     * Write an int.
     *
     * @param i The int to be written.
     */
    public void writeInt(int i) {
        bytes[offset + 3] = (byte) (i & 255);
        int w = i >> 8;
        bytes[offset + 2] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 1] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset] = (byte) (w & 255);
        offset += 4;
    }

    /**
     * Write a long.
     *
     * @param l The long to be written.
     */
    public void writeLong(long l) {
        bytes[offset + 7] = (byte) (l & 255);
        long w = l >> 8;
        bytes[offset + 6] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 5] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 4] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 3] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 2] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset + 1] = (byte) (w & 255);
        w = w >> 8;
        bytes[offset] = (byte) (w & 255);
        offset += 8;
    }

    /**
     * Write a char.
     *
     * @param c The char to be written.
     */
    public void writeChar(char c) {
        writeByte((byte) (255 & (c >> 8)));
        writeByte((byte) (255 & c));
    }

    /**
     * Write an array of char.
     *
     * @param ca The array to be written.
     */
    public void writeChars(char[] ca) {
        int i = 0;
        while (i < ca.length) {
            writeChar(ca[i]);
            i += 1;
        }
    }

    /**
     * Write a string as an int and a char array.
     * (This approach uses more bytes than other approaches but is not so computationally intensive
     * while preserving the full range of character values.)
     *
     * @param s The string to be written, or null.
     */
    public void writeString(String s) {
        if (s == null) {
            writeInt(-1);
            return;
        }
        writeInt(s.length() * 2);
        if (s.length() == 0)
            return;
        writeChars(s.toCharArray());
    }

    /**
     * Write a float.
     *
     * @param f The float to be written.
     */
    public void writeFloat(float f) {
        int i = Float.floatToIntBits(f);
        writeInt(i);
    }

    /**
     * Write a double.
     *
     * @param d The double to be written.
     */
    public void writeDouble(double d) {
        long l = Double.doubleToLongBits(d);
        writeLong(l);
    }
}
