package org.agilewiki.jactor2.core.blades.requestBus;

public interface Content<FILTER> {
    boolean match(final FILTER filter);
}
