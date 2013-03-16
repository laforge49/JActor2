package org.agilewiki.pactor;

public abstract class ExceptionHandler {
    public abstract void processException(final Throwable throwable)
            throws Exception;
}
