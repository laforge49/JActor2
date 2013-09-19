package org.agilewiki.jactor2.core.blades.misc;

//Prints a banner without allowing any intervening lines.
public class PrinterAgentSample extends PrinterAgent {
    //Creates a PrinterAgentSample.
    public PrinterAgentSample(Printer _printer) throws Exception {
        super(_printer);
    }

    // Returns a request to print a Hi! banner.
    public AsyncBladeRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                println("*********");
                println("*       *");
                println("*  Hi!  *");
                println("*       *");
                println("*********");
                processAsyncResponse(null);
            }
        };
    }
}
