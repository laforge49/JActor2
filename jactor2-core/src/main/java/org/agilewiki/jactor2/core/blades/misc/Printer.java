package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.io.PrintStream;
import java.util.Locale;

/**
 * An isolation bladefor printing.
 * By using an isolation blade, printing is done on a different thread, along with
 * formatting. In effect, Printer implements a simple logger.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class PrinterSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A facility with one thread.
 *         final Facility facility = new Facility(1);
 *
 *         try {
 *
 *             //Create a Printer.
 *             Printer printer = new Printer(new IsolationReactor(facility));
 *
 *             //Print something.
 *             printer.printlnSReq("Hello World!").call();
 *
 *         } finally {
 *             //shutdown the facility
 *             facility.close();
 *         }
 *
 *     }
 * }
 * </pre>
 */
public class Printer extends IsolationBlade {

    final public PrintStream printStream;

    final public Locale locale;

    /**
     * Create a Printer blade.
     *
     * @param _isolationReactor The reactor used by the isolation blade.
     */
    public Printer(final IsolationReactor _isolationReactor) throws Exception {
        this(_isolationReactor, System.out);
    }

    /**
     * Create a Printer blade.
     *
     * @param _isolationReactor The reactor used by the isolation blade.
     * @param _printStream      Where to print the string.
     */
    public Printer(final IsolationReactor _isolationReactor,
                   final PrintStream _printStream) throws Exception {
        this(_isolationReactor, _printStream, null);
    }

    /**
     * Create a Printer blade.
     *
     * @param _isolationReactor The reactor used by the isolation blade.
     * @param _printStream      Where to print the string.
     */
    public Printer(final IsolationReactor _isolationReactor,
                   final PrintStream _printStream,
                   final Locale _locale) throws Exception {
        super(_isolationReactor);
        printStream = _printStream;
        locale = _locale;
    }

    /**
     * A request to print a string.
     *
     * @param _string The string to be printed
     * @return The request.
     */
    public SyncRequest<Void> printlnSReq(final String _string) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                System.out.println(_string);
                return null;
            }
        };
    }

    /**
     * A request to print a formated string.
     *
     * @param _format The formatting.
     * @param _args   The data to be formatted.
     * @return The request.
     */
    public SyncRequest<Void> printSReq(final String _format,
                                       final Object... _args) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                printStream.print(String.format(locale, _format, _args));
                return null;
            }
        };
    }
}
