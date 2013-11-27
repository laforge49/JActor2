package org.agilewiki.jactor2.core.util;

public class DefaultRecovery implements Recovery {
    @Override
    public long getThreadInterruptMilliseconds() {
        return 3000;
    }
}
