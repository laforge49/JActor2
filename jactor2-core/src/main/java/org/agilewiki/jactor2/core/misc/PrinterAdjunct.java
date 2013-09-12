package org.agilewiki.jactor2.core.misc;

import org.agilewiki.jactor2.core.ActorBase;

/**
 * A PrinterAdjunct can be used to print multiple lines
 * without having any other text being interleaved by other actors
 * using the same Printer.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * //Prints a banner without allowing any intervening lines.
 * public class PrinterAdjunctSample extends PrinterAdjunct {
 *
 *     public PrinterAdjunctSample(Printer _printer) throws Exception {
 *         psuper(_printer);
 *     }
 *
 *     // Returns a request to print a Hi! banner.
 *     public SyncRequest&lt;Void&gt; hiSReq() {
 *         return new SyncRequest&lt;Void&gt;(getReactor()) {
 *             {@literal @}Override
 *             public Void processSyncRequest() throws Exception {
 *                 printer.printlnSReq("*********").local(messageProcessor);
 *                 printer.printlnSReq("*       *").local(messageProcessor);
 *                 printer.printlnSReq("*  Hi!  *").local(messageProcessor);
 *                 printer.printlnSReq("*       *").local(messageProcessor);
 *                 printer.printlnSReq("*********").local(messageProcessor);
 *                 return null;
 *             }
 *         };
 *     }
 * }
 * </pre>
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
        initialize(_printer.getReactor());
        printer = _printer;
    }
}
