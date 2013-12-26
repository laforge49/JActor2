package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.impl.ThreadBoundReactorImpl;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;

/**
 * A targetReactor bound to a pre-existing thread, a thread-bound targetReactor can use
 * a program's main thread or a GUI thread.
 * <p>
 * For thread safety, the processing of each message is done in isolation, but when the processing of a
 * message results in the sending of a request, other messages may be processed before a
 * response to that request is received.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different targetReactor are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when all
 * incoming messages have been processed.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.Plant;
 * import org.agilewiki.jactor2.core.messaging.Event;
 *
 * public class ThreadBoundMessageProcessorSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A facility with no threads
 *         final Plant plant = new Plant(0);
 *
 *         //Get a reference to the main thread
 *         final Thread mainThread = Thread.currentThread();
 *
 *         //Create a thread-bound targetReactor.
 *         final ThreadBoundReactor boundMessageProcessor =
 *             new ThreadBoundReactor(plant, new Runnable() {
 *                 {@literal @}Override
 *                 public void run() {
 *                     //Interrupt the main thread when there are messages to process
 *                     mainThread.interrupt();
 *                 }
 *             });
 *
 *         //Create an blades that uses the thread-bound targetReactor.
 *         final ThreadBoundBlade threadBoundBlade = new ThreadBoundBlade(boundMessageProcessor);
 *
 *         //Terminate the blades.
 *         new SyncRequest&lt;Void&gt;(threadBoundBlade.getReactor()) {
 *
 *             {@literal @}Override
 *             protected Void processSyncRequest() throws Exception {
 *                 threadBoundBlade.fin();
 *                 return null;
 *             }
 *         }.signal();
 *
 *         //Process messages when this thread is interrupted
 *         while (true) {
 *             try {
 *                 //Wait for an interrupt
 *                 Thread.sleep(60000);
 *             } catch (InterruptedException e) {
 *                 //Process messages when the main thread is interrupted
 *                 boundMessageProcessor.run();
 *             }
 *         }
 *     }
 * }
 *
 * class ThreadBoundBlade extends BladeBase {
 *
 *     ThreadBoundBlade(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Print "finished" and exit when fin is called
 *     void fin() throws Exception {
 *         System.out.println("finished");
 *         System.exit(0);
 *     }
 * }
 *
 * Output:
 * finished
 * </pre>
 */
public class ThreadBoundReactor extends ReactorBase implements CommonReactor, Runnable {

    public ThreadBoundReactor(final Runnable _boundProcessor) throws Exception {
        this(Plant.getSingleton().asFacility(), _boundProcessor);
    }

    public ThreadBoundReactor(final Facility _facility,
                              final Runnable _boundProcessor) throws Exception {
        this(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize(), _boundProcessor);
    }

    public ThreadBoundReactor(final BasicPlant _plant,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _boundProcessor) throws Exception {
        this(_plant.asFacility(), _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
    }

    public ThreadBoundReactor(final Facility _facility,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _boundProcessor) throws Exception {
        super(new ThreadBoundReactorImpl(_facility, _initialOutboxSize, _initialLocalQueueSize, _boundProcessor));
    }

    @Override
    public void run() {
        asReactorImpl().run();
    }
}
