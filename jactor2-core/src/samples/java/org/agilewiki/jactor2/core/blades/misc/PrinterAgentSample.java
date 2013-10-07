package org.agilewiki.jactor2.core.blades.misc;

//Prints a banner without allowing any intervening lines.
@Deprecated
public class PrinterAgentSample extends PrinterAgent {
    //Creates a PrinterAgentSample.
    public PrinterAgentSample(Printer _printer) throws Exception {
        super(_printer);
    }

    // Print a Hi! banner.
    public Void start() throws Exception {
        println("*********");
        println("*       *");
        println("*  Hi!  *");
        println("*       *");
        println("*********");
        return null;
    }
}
