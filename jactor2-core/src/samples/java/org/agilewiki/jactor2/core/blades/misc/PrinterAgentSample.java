package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

//Prints a banner without allowing any intervening lines.
public class PrinterAgentSample extends PrinterAgent {
    //Creates a PrinterAgentSample.
    public PrinterAgentSample(Printer _printer) throws Exception {
        super(_printer);
    }

    // Returns a request to print a Hi! banner.
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                printer.printlnSReq("*********").local(messageProcessor);
                printer.printlnSReq("*       *").local(messageProcessor);
                printer.printlnSReq("*  Hi!  *").local(messageProcessor);
                printer.printlnSReq("*       *").local(messageProcessor);
                printer.printlnSReq("*********").local(messageProcessor);
                processAsyncResponse(null);
            }
        };
    }
}
