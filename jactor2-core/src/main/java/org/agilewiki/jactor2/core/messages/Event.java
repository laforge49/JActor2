package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * An Event instance is used to pass one-way unbuffered messages to any number of Blade objects.
 * Event messages are unbuffered and are sent immediately. The net effect of sending
 * an event to a blade is that Event.processEvent, an application-specific method,
 * is called in a thread-safe way from the blade's Reactor's own thread.
 * <p>
 * As neither message buffering nor thread migration are used, events may be slower,
 * in terms of both latency and throughput, than a request. On the other hand, when
 * the target Reactor is isolation, event processing is not delayed until a response is
 * assigned to a prior request.
 * </p>
 * <p>
 * Some care needs to be taken with the parameters passed to the constructor of an
 * event. Either the constructor needs to perform a deep copy of these parameters
 * or the application must take care not to change the contents of these parameters,
 * as their will likely be accessed from a different thread when the target blade
 * is operated on.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.Plant;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * public class EventSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A facility with one thread.
 *         final Plant plant = new Plant(1);
 *
 *         //Create a SampleBlade1 instance.
 *         SampleBlade1 sampleBlade1 = new SampleBlade1(new NonBlockingReactor(plant));
 *
 *         //Print "finished" and exit when the event is processed by SampleBlade1.
 *         new FinEvent("finished").signal(sampleBlade1);
 *
 *         //Hang until exit.
 *         Thread.sleep(1000000);
 *
 *         }
 *     }
 *
 *     class SampleBlade1 extends BladeBase {
 *
 *         SampleBlade1(final Reactor _messageProcessor) throws Exception {
 *             initialize(_messageProcessor);
 *         }
 *
 *         void fin(final String msg) throws Exception {
 *             System.out.println(msg);
 *             System.exit(0);
 *         }
 *     }
 *
 *     //When a FinEvent is passed to a blade, the fin method is called.
 *     class FinEvent extends Event<SampleBlade1> {
 *         private final String msg;
 *
 *         FinEvent(final String _msg) {
 *             msg = _msg;
 *         }
 *
 *         {@literal @}Override
 *         public void processEvent(SampleBlade1 _targetBlade) throws Exception {
 *             _targetBlade.fin(msg);
 *         }
 *     }
 *
 * Output:
 * finished
 * </pre>
 *
 * @param <TARGET_BLADE_TYPE> The class of the blade that will be targeted when this Event is passed.
 */
@Deprecated
public abstract class Event<TARGET_BLADE_TYPE extends Blade> {

    /**
     * Passes an event message immediately to the target Reactor for subsequent processing
     * by the thread of the that targetReactor. No result is passed back and if an exception is
     * thrown while processing the event,that exception is simply logged as a warning.
     *
     * @param _targetBlade The actor to be operated on.
     */
    final public void signal(final TARGET_BLADE_TYPE _targetBlade) throws Exception {
        final EventMessage message = new EventMessage(_targetBlade);
        ((ReactorBase) _targetBlade.getReactor()).unbufferedAddMessage(message, false);
    }

    /**
     * The processEvent method will be invoked by the target Reactor on its own thread
     * when this event is processed.
     *
     * @param _targetBlade The actor to be operated on.
     */
    abstract protected void processEvent(final TARGET_BLADE_TYPE _targetBlade)
            throws Exception;

    /**
     * The message subclass used to pass events. Event messages are not reused, with a
     * new event message being created each time Event.signal is called.
     */
    final private class EventMessage implements Message {

        /**
         * The blade to be operated on.
         */
        final TARGET_BLADE_TYPE targetBlade;

        /**
         * Create an EventMessage.
         *
         * @param _targetBlade The blade to be operated on.
         */
        EventMessage(final TARGET_BLADE_TYPE _targetBlade) {
            targetBlade = _targetBlade;
        }

        @Override
        public boolean isForeign() {
            return false;
        }

        @Override
        public boolean isResponsePending() {
            return false;
        }

        @Override
        public boolean isIsolated() {
            return false;
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public void eval() {
            ReactorBase targetMessageProcessor = (ReactorBase) targetBlade.getReactor();
            targetMessageProcessor.setExceptionHandler(null);
            targetMessageProcessor.setCurrentMessage(this);
            try {
                processEvent(targetBlade);
            } catch (final Exception e) {
                processException(targetMessageProcessor, e);
            }
        }

        @Override
        public void processException(final Reactor _activeReactor, final Exception _e) {
            ReactorBase activeMessageProcessor = (ReactorBase) _activeReactor;
            ExceptionHandler exceptionHandler = activeMessageProcessor.getExceptionHandler();
            if (exceptionHandler != null) {
                try {
                    exceptionHandler.processException(_e);
                } catch (final Throwable u) {
                    activeMessageProcessor.getLogger().error("Exception handler unable to process throwable "
                            + exceptionHandler.getClass().getName(), u);
                    activeMessageProcessor.getLogger().error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _e);
                }
            }
        }
    }
}
