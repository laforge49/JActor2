package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.Agent;
import org.agilewiki.jactor2.core.blades.BladeBase;

/**
 * A PrinterAgent can be used to print multiple lines
 * without having any other text being interleaved by other blades
 * using the same Printer.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * //Prints a banner without allowing any intervening lines.
 * public class PrinterAgentSample extends PrinterAgent {
 *
 *     public PrinterAgentSample(Printer _printer) throws Exception {
 *         psuper(_printer);
 *     }
 *
 *     // Returns a request to print a Hi! banner.
 *     protected AsyncRequest&lt;Void&gt; startAReq() {
 *         return new AsyncBladeRequest&lt;Void&gt;() {
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *                 printer.printlnSReq("*********").local(messageProcessor);
 *                 printer.printlnSReq("*       *").local(messageProcessor);
 *                 printer.printlnSReq("*  Hi!  *").local(messageProcessor);
 *                 printer.printlnSReq("*       *").local(messageProcessor);
 *                 printer.printlnSReq("*********").local(messageProcessor);
 *                 processAsyncResponse(null);
 *             }
 *         };
 *     }
 * }
 * </pre>
 */
abstract public class PrinterAgent extends BladeBase implements Agent<Void> {
    /**
     * The printer used to print the text.
     */
    public final Printer printer;

    /**
     * Create a printer adjunct.
     *
     * @param _printer The printer used to print the text.
     */
    public PrinterAgent(final Printer _printer) throws Exception {
        initialize(_printer.getReactor());
        printer = _printer;
    }
}
