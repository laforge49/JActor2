package org.agilewiki.jactor2.core.blades.misc;

import java.io.PrintStream;
import java.util.Locale;

import org.agilewiki.jactor2.core.blades.BlockingBlade;
import org.agilewiki.jactor2.core.blades.oldTransactions.oldProperties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.blades.oldTransactions.oldProperties.PropertiesWrapper;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * A blocking blade is used for printing.
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
public class Printer extends BlockingBlade {

    public static AsyncRequest<Void> printlnAReq(final Facility _facility,
            final String _string) throws Exception {
        return new AsyncRequest<Void>(_facility.getReactor()) {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                Facility facility = _facility;
                final Plant plant = facility.getPlant();
                if (plant != null) {
                    facility = plant;
                }
                send(stdoutAReq((Plant) facility),
                        new AsyncResponseProcessor<Printer>() {
                            @Override
                            public void processAsyncResponse(
                                    final Printer _printer) throws Exception {
                                send(_printer.printlnSReq(_string), dis);
                            }
                        });
            }
        };
    }

    public static AsyncRequest<Void> printfAReq(final Facility _facility,
            final String _format, final Object... _args) throws Exception {
        return new AsyncRequest<Void>(_facility.getReactor()) {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                Facility facility = _facility;
                final Plant plant = facility.getPlant();
                if (plant != null) {
                    facility = plant;
                }
                send(stdoutAReq((Plant) facility),
                        new AsyncResponseProcessor<Printer>() {
                            @Override
                            public void processAsyncResponse(
                                    final Printer _printer) throws Exception {
                                send(_printer.printfSReq(_format, _args), dis);
                            }
                        });
            }
        };
    }

    public static AsyncRequest<Printer> stdoutAReq(final Plant _plant)
            throws Exception {
        return new AsyncRequest<Printer>(_plant.getReactor()) {
            final AsyncResponseProcessor<Printer> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                final Printer printer = (Printer) _plant.getProperty("stdout");
                if (printer != null) {
                    processAsyncResponse(printer);
                    return;
                }
                send(createStdoutAReq(_plant),
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(
                                    final Void _response) throws Exception {
                                dis.processAsyncResponse((Printer) _plant
                                        .getProperty("stdout"));
                            }
                        });
            }
        };
    }

    static private AsyncRequest<Void> createStdoutAReq(final Plant _plant)
            throws Exception {
        return new PropertiesTransactionAReq(
                (NonBlockingReactor) _plant.getReactor(),
                _plant.getPropertiesBlade()) {
            @Override
            protected void evalTransaction(
                    final PropertiesWrapper _stateWrapper,
                    final AsyncResponseProcessor<Void> _rp) throws Exception {
                if (!_stateWrapper.oldReadOnlyProperties.containsKey("stdout")) {
                    _stateWrapper.put("stdout", new Printer(
                            new BlockingReactor(_plant)));
                    _plant.getPropertiesBlade().writeOnceProperty("stdout")
                            .signal();
                }
                _rp.processAsyncResponse(null);
            }
        };
    }

    public final PrintStream printStream;

    public final Locale locale;

    /**
     * Create a Printer blades.
     *
     * @param _reactor The reactor used by the blocking blade.
     */
    public Printer(final BlockingReactor _reactor) throws Exception {
        this(_reactor, System.out);
    }

    /**
     * Create a Printer blades.
     *
     * @param _reactor     The reactor used by the blocking blade.
     * @param _printStream Where to print the string.
     */
    public Printer(final BlockingReactor _reactor,
            final PrintStream _printStream) throws Exception {
        this(_reactor, _printStream, null);
    }

    /**
     * Create a Printer blades.
     *
     * @param _reactor     The reactor used by the blocking blade.
     * @param _printStream Where to print the string.
     */
    public Printer(final BlockingReactor _reactor,
            final PrintStream _printStream, final Locale _locale)
            throws Exception {
        super(_reactor);
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
