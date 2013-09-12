package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.misc.Printer;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class PrinterSample {

    public static void main(String[] args) throws Exception {

        //A context with one thread.
        final ModuleContext moduleContext = new ModuleContext(1);

        try {

            //Create a Printer.
            Printer printer = new Printer(moduleContext);

            //Print something.
            printer.printlnSReq("Hello World!").call();

        } finally {
            //shutdown the context
            moduleContext.close();
        }

    }
}
