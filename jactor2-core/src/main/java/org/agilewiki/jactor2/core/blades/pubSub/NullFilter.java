package org.agilewiki.jactor2.core.blades.pubSub;

/**
 * Used when all content is of interest.
 */
public class NullFilter<CONTENT> implements Filter<CONTENT> {
    @Override
    public boolean match(Object _content) {
        return true;
    }
}
