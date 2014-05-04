package org.agilewiki.jactor2.core.blades.transactions;

public class RuntimeWrapperException extends Exception {
    public RuntimeWrapperException(RuntimeException cause) {
        super(cause);
    }
}
