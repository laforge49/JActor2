package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;

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
 *         //Create an blade that uses the thread-bound targetReactor.
 *         final ThreadBoundBlade threadBoundBlade = new ThreadBoundBlade(boundMessageProcessor);
 *
 *         //Pass a FinEvent signal to the blade
 *         new FinEvent().signal(threadBoundBlade);
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
 * //When a FinEvent is passed to an blade, the fin method is called
 * class FinEvent extends Event&lt;ThreadBoundBlade&gt; {
 *     {@literal @}Override
 *     public void processEvent(ThreadBoundBlade _targetBlade) throws Exception {
 *         _targetBlade.fin();
 *     }
 * }
 *
 * Output:
 * finished
 * </pre>
 */
public class ThreadBoundReactor extends ReactorBase {

    /**
     * The boundProcessor.run method is called when there are messages to be processed.
     */
    private final Runnable boundProcessor;

    /**
     * Create a thread-bound targetReactor.
     * <p>
     * The _boundProcessor.run method is called when a thread-bound targetReactor has messages
     * that need processing. As a result of invoking the run method, the
     * ThreadBoundReactor.run method must subsequently to be invoked by the thread that
     * the targetReactor is bound to.
     * </p>
     *
     * @param _facility       The facility of the targetReactor.
     * @param _boundProcessor The _messageProcessor.run method is called when there
     *                        are messages to be processed.
     */
    public ThreadBoundReactor(Facility _facility, Runnable _boundProcessor) throws Exception {
        super(_facility,
                _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize());
        boundProcessor = _boundProcessor;
    }

    /**
     * Create a thread-bound targetReactor.
     * <p>
     * The boundProcessor.run method is called when a thread-bound targetReactor has messages
     * that need processing. As a result of invoking the run method, the
     * ThreadBoundReactor.run method must subsequently to be invoked by the thread that
     * the targetReactor is bound to.
     * </p>
     *
     * @param _facility              The facility of the targetReactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     * @param _boundProcessor        The _messageProcessor.run method is called when there
     *                               are messages to be processed.
     */
    public ThreadBoundReactor(Facility _facility,
                              int _initialOutboxSize,
                              final int _initialLocalQueueSize,
                              Runnable _boundProcessor) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize);
        boundProcessor = _boundProcessor;
    }

    @Override
    protected void notBusy() throws Exception {
        flush();
    }

    @Override
    public final boolean isRunning() {
        return true;
    }

    @Override
    public AtomicReference<Thread> getThreadReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIdler() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
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
        final Iterator<Map.Entry<ReactorBase, ArrayDeque<Message>>> iter = outbox.getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorBase, ArrayDeque<Message>> entry = iter.next();
                final ReactorBase target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                try {
                    notBusy();
                } catch (Exception e) {
                    log.error("Exception thrown by onIdle", e);
                }
                if (hasWork())
                    continue;
                return;
            }
            processMessage(message);
        }
    }
}
