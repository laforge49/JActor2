package org.agilewiki.jactor2.api;

/**
 * Request objects are typically created as an anonymous class within the targeted Actor and bound
 * to that actor's mailbox. By this means the request can update an actor's state in a thread-safe way.
 * <p/>
 * <pre>
 *     public class ActorA {
 *         private final Mailbox mailbox;
 *         public final Request&lt;String&gt; hi1;
 *
 *         public Actor1(final Mailbox _mailbox) {
 *             mailbox = _mailbox;
 *
 *             hi1 = new RequestBase&lt;String&gt;(mailbox) {
 *                 public void processRequest(final ResponseProcessor&lt;String&gt; _rp)
 *                         throws Exception {
 *                     responseProcessor.processResponse("Hello world!");
 *                }
 *             };
 *         }
 *     }
 * </pre>
 *
 * @param <RESPONSE_TYPE> The class of the result returned when this Request is processed.
 */
public abstract class RequestBase<RESPONSE_TYPE> implements
        Request<RESPONSE_TYPE>, _Request<RESPONSE_TYPE, Actor> {

    /**
     * The mailbox where this Request Objects is passed for processing. The thread
     * owned by this mailbox will process this Request.
     */
    private final Mailbox mailbox;

    /**
     * Create an Request and bind it to its target mailbox.
     *
     * @param _targetMailbox The mailbox where this Request Objects is passed for processing.
     *                       The thread owned by this mailbox will process this Request.
     */
    public RequestBase(final Mailbox _targetMailbox) {
        if (_targetMailbox == null) {
            throw new NullPointerException("targetMailbox");
        }
        this.mailbox = _targetMailbox;
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    @Override
    public void signal(final Mailbox _source) throws Exception {
        if (!_source.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final Message message = new Message(false, mailbox, null, null,
                this, null, EventResponseProcessor.SINGLETON);
        message.signal(mailbox);
    }

    @Override
    public void send(final Mailbox _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        _source.sendTo(this, mailbox, null, _rp);
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        final Caller caller = new Caller();
        final Message message = new Message(true, caller, null, null,
                this, null, DummyResponseProcessor.SINGLETON);
        return mailbox.call(this, null);
    }

    @Override
    public final void processRequest(final Actor _targetActor,
                                     final Transport<RESPONSE_TYPE> _transport) throws Exception {
        processRequest(_transport);
    }
}
