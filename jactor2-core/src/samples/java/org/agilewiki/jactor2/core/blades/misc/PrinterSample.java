package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.facilities.Facility;

public class PrinterSample {

    public static void main(String[] args) throws Exception {

        //A facility with one thread.
        final Facility facility = new Facility(1);

        try {

            //Print something.
            Printer.printlnAReq(facility, "Hello World!").call();

        } finally {
            //shutdown the facility
            facility.close();
        }

    }
}
