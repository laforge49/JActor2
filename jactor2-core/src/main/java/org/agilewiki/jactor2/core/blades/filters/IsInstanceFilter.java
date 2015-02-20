package org.agilewiki.jactor2.core.blades.filters;

import org.agilewiki.jactor2.core.GwtIncompatible;

/**
 * Used to select content of a particular class.
 *
 * @param <CONTENT> The content class of interest.
 */
@GwtIncompatible
public class IsInstanceFilter<CONTENT> implements Filter<CONTENT> {
    private final Class<? extends CONTENT> clazz;

    /**
     * Create an IsInstanceFilter.
     *
     * @param _clazz The particular class of interest.
     */
    public IsInstanceFilter(final Class<? extends CONTENT> _clazz) {
        clazz = _clazz;
    }

    @Override
    public boolean match(final Object _content) {
        return clazz.isInstance(_content);
    }
}
