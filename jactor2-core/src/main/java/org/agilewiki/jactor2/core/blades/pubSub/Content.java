package org.agilewiki.jactor2.core.blades.pubSub;

public interface Content<FILTER> {
    boolean match(final FILTER filter);
}
