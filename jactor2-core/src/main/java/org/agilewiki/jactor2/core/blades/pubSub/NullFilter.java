package org.agilewiki.jactor2.core.blades.pubSub;

public class NullFilter implements Filter {
    @Override
    public boolean match(Object _content) {
        return true;
    }
}
