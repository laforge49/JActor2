package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.threading.ModuleContext;

import java.io.PrintStream;
import java.util.Locale;

/**
 * An isolation actor for printing.
 * By using an isolation actor, printing is done on a different thread, along with
 * formatting. In effect, Printer implements a simple logger.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class PrinterSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A context with one thread.
 *         final ModuleContext moduleContext = new ModuleContext(1);
 *
 *         try {
 *
 *             //Create a Printer.
 *             Printer printer = new Printer(moduleContext);
 *
 *             //Print something.
 *             printer.printlnSReq("Hello World!").call();
 *
 *         } finally {
 *             //shutdown the context
 *             moduleContext.close();
 *         }
 *
 *     }
 * }
 * </pre>
 */
public class Printer extends IsolationActor {
    /**
     * Create a Printer actor.
     *
     * @param _moduleContext A set of resources, including a thread pool, for use
     *                       by message processors and their actors.
     */
    public Printer(ModuleContext _moduleContext) throws Exception {
        super(_moduleContext);
    }

    /**
     * A request to print a string.
     *
     * @param _string The string to be printed
     * @return The request.
     */
    public SyncRequest<Void> printlnSReq(final String _string) {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                System.out.println(_string);
                return null;
            }
        };
    }

    /**
     * A request to print a string.
     *
     * @param _printStream Where to print the string.
     * @param _string      The string to be printed
     * @return The request.
     */
    public SyncRequest<Void> printSReq(final PrintStream _printStream,
                                       final String _string) {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                _printStream.print(_string);
                return null;
            }
        };
    }

    /**
     * A request to print a formated string.
     *
     * @param _printStream Where to print the string.
     * @param _format      The formatting.
     * @param _args        The data to be formatted.
     * @return The request.
     */
    public SyncRequest<Void> printSReq(final PrintStream _printStream,
                                       final String _format,
                                       final Object... _args) {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                _printStream.print(String.format(_format, _args));
                return null;
            }
        };
    }

    /**
     * A request to print a formated string.
     *
     * @param _printStream Where to print the string.
     * @param _locale      The locale to apply during formatting.
     * @param _format      The formatting.
     * @param _args        The data to be formatted.
     * @return The request.
     */
    public SyncRequest<Void> printSReq(final PrintStream _printStream,
                                       final Locale _locale,
                                       final String _format,
                                       final Object... _args) {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                _printStream.print(String.format(_locale, _format, _args));
                return null;
            }
        };
    }
}
