package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.SyncRequest;

//Prints a banner without allowing any intervening lines.
public class PrinterAdjunctSample extends PrinterAdjunct {
    //Creates a PrinterAdjunctSample.
    public PrinterAdjunctSample(Printer _printer) throws Exception {
        super(_printer);
    }

    // Returns a request to print a Hi! banner.
    public SyncRequest<Void> hiSReq() {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                printer.printlnSReq("*********").local(messageProcessor);
                printer.printlnSReq("*       *").local(messageProcessor);
                printer.printlnSReq("*  Hi!  *").local(messageProcessor);
                printer.printlnSReq("*       *").local(messageProcessor);
                printer.printlnSReq("*********").local(messageProcessor);
                return null;
            }
        };
    }
}
