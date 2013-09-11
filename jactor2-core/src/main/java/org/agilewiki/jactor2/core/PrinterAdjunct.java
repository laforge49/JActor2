package org.agilewiki.jactor2.core;

/**
 * A PrinterAdjunct can be used to print multiple lines
 * without having any other text being interleaved by other actors
 * using the same Printer.
 */
public class PrinterAdjunct extends ActorBase {
    /**
     * The printer used to print the text.
     */
    public final Printer printer;

    /**
     * Create a printer adjunct.
     *
     * @param _printer The printer used to print the text.
     */
    public PrinterAdjunct(final Printer _printer) throws Exception {
        initialize(_printer.getMessageProcessor());
        printer = _printer;
    }
}
