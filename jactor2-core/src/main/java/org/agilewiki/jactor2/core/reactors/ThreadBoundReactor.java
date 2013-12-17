package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.PoolThread;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
public class ThreadBoundReactor extends ReactorImpl implements CommonReactor, Reactor {

    /**
     * The boundProcessor.run method is called when there are messages to be processed.
     */
    private final Runnable boundProcessor;

    public ThreadBoundReactor(final BasicPlant _plant,
                              final Runnable _boundProcessor) throws Exception {
        this(_plant.asFacility(), _boundProcessor);
    }

    public ThreadBoundReactor(final Facility _facility,
                              final Runnable _boundProcessor) throws Exception {
        super(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize());
        initialize(this);
        boundProcessor = _boundProcessor;
    }

    public ThreadBoundReactor(final BasicPlant _plant,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _boundProcessor) throws Exception {
        this(_plant.asFacility(), _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
    }

    public ThreadBoundReactor(final Facility _facility,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _boundProcessor) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize);
        initialize(this);
        boundProcessor = _boundProcessor;
    }

    @Override
    protected void notBusy() throws Exception {
        flush();
    }

    @Override
    public AtomicReference<PoolThread> getThreadReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIdler() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }

    @Override
    protected void afterAdd() throws Exception {
        boundProcessor.run();
    }

    /**
     * The flush method disburses all buffered message to their target targetReactor for
     * processing.
     * <p>
     * The flush method is automatically called when there are
     * no more messages to be processed.
     * </p>
     *
     * @return True when one or more buffered messages were delivered.
     */
    public final boolean flush() throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<ReactorImpl, ArrayDeque<Message>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorImpl, ArrayDeque<Message>> entry = iter
                        .next();
                final ReactorImpl target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
