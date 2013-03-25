package org.agilewiki.pactor;

public interface ExceptionHandler {
    public abstract void processException(final Throwable throwable)
            throws Exception;
}
