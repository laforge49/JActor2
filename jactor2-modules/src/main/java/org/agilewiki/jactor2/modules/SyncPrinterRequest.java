package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Base class for printer requests.
 */
abstract public class SyncPrinterRequest extends SyncRequest<Void> {

    final protected Printer printer;

    /**
     * Create a SyncPrinterRequest.
     *
     * @param _printer The blades this request is bound to.
     */
    public SyncPrinterRequest(final Printer _printer) {
        super(_printer.getReactor());
        printer = _printer;
    }

    /**
     * Print a string.
     *
     * @param _string The string to be printed
     */
    protected void println(final String _string) throws Exception {
        local(printer.printlnSReq(_string));
    }

    /**
     * Print a formatted string.
     *
     * @param _format The formatting.
     * @param _args   The data to be formatted.
     */
    protected void printf(final String _format, final Object... _args)
            throws Exception {
        local(printer.printfSReq(_format, _args));
    }
}
