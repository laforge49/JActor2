package org.agilewiki.pactor.utilImpl.durable;

import org.agilewiki.pactor.util.durable.incDes.PAInteger;

public class DurablesImpl {

    /**
     * Returns the number of bytes needed to write a string.
     *
     * @param _length The number of characters in the string.
     * @return The size in bytes.
     */
    public final static int stringLength(final int _length) {
        if (_length == -1)
            return PAInteger.LENGTH;
        if (_length > -1)
            return PAInteger.LENGTH + 2 * _length;
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
            return PAInteger.LENGTH;
        return stringLength(_s.length());
    }
}
