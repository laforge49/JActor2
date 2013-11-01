package org.agilewiki.jactor2.core.blades.pubSub;

public interface Filter<CONTENT> {
    boolean match(CONTENT _content);
}
