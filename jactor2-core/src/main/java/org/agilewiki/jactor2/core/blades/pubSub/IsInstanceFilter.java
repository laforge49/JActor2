package org.agilewiki.jactor2.core.blades.pubSub;

/**
 * Used to select content of a particular class.
 *
 * @param <CONTENT> The content class of interest.
 */
public class IsInstanceFilter<CONTENT> implements Filter<CONTENT> {
    private final Class<CONTENT> clazz;

    /**
     * Create an IsInstanceFilter.
     *
     * @param _clazz The particular class of interest.
     */
    public IsInstanceFilter(final Class<CONTENT> _clazz) {
        clazz = _clazz;
    }

    @Override
    public boolean match(Object _content) {
        return clazz.isInstance(_content);
    }
}
