package org.agilewiki.jactor.impl;

public class MigrateException extends Exception {
    public MigrateException singleton = new MigrateException();

    /**
     * Speeds things up by not filling in the stack trace.
     *
     * @return this
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
