package org.agilewiki.jactor2.core.blades.misc;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesWrapper;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.io.PrintStream;
import java.util.Locale;

/**
 * An isolation blade is used for printing.
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
 *         final Plant plant = new Plant(1);
 *
 *         try {
 *
 *             //Print something.
 *             Printer.printlnAReq(plant, "Hello World!").call();
 *
 *         } finally {
 *             //shutdown the plant
 *             plant.close();
 *         }
 *
 *     }
 * }
 * </pre>
 */
public class Printer extends IsolationBlade {

    public static AsyncRequest<Void> printlnAReq(final Facility _facility, final String _string) throws Exception {
        return new AsyncRequest<Void>(_facility.getReactor()) {
            AsyncResponseProcessor<Void> dis = this;

            public void processAsyncRequest() throws Exception {
                Facility facility = _facility;
                Plant plant = facility.getPlant();
                if (plant != null)
                    facility = plant;
                send(stdoutAReq((Plant) facility), new AsyncResponseProcessor<Printer>() {
                    @Override
                    public void processAsyncResponse(Printer _printer) throws Exception {
                        send(_printer.printlnSReq(_string), dis);
                    }
                });
            }
        };
    }

    public static AsyncRequest<Void> printfAReq(final Facility _facility, final String _format, final Object... _args) throws Exception {
        return new AsyncRequest<Void>(_facility.getReactor()) {
            AsyncResponseProcessor<Void> dis = this;

            public void processAsyncRequest() throws Exception {
                Facility facility = _facility;
                Plant plant = facility.getPlant();
                if (plant != null)
                    facility = plant;
                send(stdoutAReq((Plant) facility), new AsyncResponseProcessor<Printer>() {
                    @Override
                    public void processAsyncResponse(Printer _printer) throws Exception {
                        send(_printer.printfSReq(_format, _args), dis);
                    }
                });
            }
        };
    }

    static public AsyncRequest<Printer> stdoutAReq(final Plant _plant) throws Exception {
        return new AsyncRequest<Printer>(_plant.getReactor()) {
            final AsyncResponseProcessor<Printer> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                Printer printer = (Printer) _plant.getProperty("stdout");
                if (printer != null) {
                    processAsyncResponse(printer);
                    return;
                }
                send(createStdoutAReq(_plant), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        dis.processAsyncResponse((Printer) _plant.getProperty("stdout"));
                    }
                });
            }
        };
    }

    static private AsyncRequest<Void> createStdoutAReq(final Plant _plant) throws Exception {
        return new PropertiesTransactionAReq((NonBlockingReactor) _plant.getReactor(),
                _plant.getPropertiesBlade()) {
            @Override
            protected void evalTransaction(final PropertiesWrapper _stateWrapper,
                                           final AsyncResponseProcessor<Void> _rp)
                    throws Exception {
                _stateWrapper.put("stdout", new Printer(new IsolationReactor(_plant)));
                _rp.processAsyncResponse(null);
            }
        };
    }

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
    public SyncRequest<Void> printfSReq(final String _format,
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
