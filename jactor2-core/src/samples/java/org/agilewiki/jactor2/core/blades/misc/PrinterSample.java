package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class PrinterSample {

    public static void main(String[] args) throws Exception {

        //A facility with one thread.
        final Facility facility = new Facility(1);

        try {

            //Create a Printer.
            Printer printer = new Printer(new IsolationReactor(facility));

            //Print something.
            printer.printlnSReq("Hello World!").call();

        } finally {
            //shutdown the facility
            facility.close();
        }

    }
}
