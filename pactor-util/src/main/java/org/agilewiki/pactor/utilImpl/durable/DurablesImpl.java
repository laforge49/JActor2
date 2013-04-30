package org.agilewiki.pactor.utilImpl.durable;

public class DurablesImpl {

    /**
     * Size of an int in bytes.
     */
    public final static int INT_LENGTH = 4;

    /**
     * Size of a long in bytes.
     */
    public final static int LONG_LENGTH = 8;

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _length The number of characters in the string.
     * @return The size in bytes.
     */
    public final static int stringLength(final int _length) {
        if (_length == -1)
            return INT_LENGTH;
        if (_length > -1)
            return INT_LENGTH + 2 * _length;
        throw new IllegalArgumentException("invalid string length: " + _length);
    }

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _s The string.
     * @return The size in bytes.
     */
    public final static int stringLength(final String _s) {
        if (_s == null)
            return INT_LENGTH;
        return stringLength(_s.length());
    }
}
