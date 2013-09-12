package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.misc.Printer;
import org.agilewiki.jactor2.core.threading.Facility;

public class PrinterSample {

    public static void main(String[] args) throws Exception {

        //A facility with one thread.
        final Facility facility = new Facility(1);

        try {

            //Create a Printer.
            Printer printer = new Printer(facility);

            //Print something.
            printer.printlnSReq("Hello World!").call();

        } finally {
            //shutdown the facility
            facility.close();
        }

    }
}
