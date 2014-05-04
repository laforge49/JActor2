package org.agilewiki.jactor2.core.blades.transactions;

public class TraceException extends Exception {
    TraceException(final String _trace, final Exception _cause) {
        super(_trace, _cause);
    }
}
