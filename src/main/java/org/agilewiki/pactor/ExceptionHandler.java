package org.agilewiki.pactor;

abstract public class ExceptionHandler {
    abstract public void processException(Throwable throwable) throws Throwable;
}
