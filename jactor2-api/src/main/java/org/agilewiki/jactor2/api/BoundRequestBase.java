package org.agilewiki.jactor2.api;

/**
 * BoundRequest objects are typically created as an anonymous class within the targeted Actor and bound
 * to that actor's mailbox. By this means the request can update an actor's state in a thread-safe way.
 * <p/>
 * <pre>
 *     public class ActorA {
 *         private final Mailbox mailbox;
 *         public final BoundRequest&lt;String&gt; hi1;
 *
 *         public Actor1(final Mailbox _mailbox) {
 *             mailbox = _mailbox;
 *
 *             hi1 = new BoundRequestBase&lt;String&gt;(mailbox) {
 *                 public void processRequest(final ResponseProcessor&lt;String&gt; _rp)
 *                         throws Exception {
 *                     responseProcessor.processResponse("Hello world!");
 *                }
 *             };
 *         }
 *     }
 * </pre>
 *
 * @param <RESPONSE_TYPE> The class of the result returned when this BoundRequest is processed.
 */
@Deprecated
public abstract class BoundRequestBase<RESPONSE_TYPE> implements
        BoundRequest<RESPONSE_TYPE>, _Request<RESPONSE_TYPE, Actor> {
    /**
     * The mailbox where this BoundRequest Objects is passed for processing. The thread
     * owned by this mailbox will process this BoundRequest.
     */
    private final Mailbox mailbox;

    /**
     * Create an BoundRequest and bind it to its target mailbox.
     *
     * @param _targetMailbox The mailbox where this BoundRequest Objects is passed for processing.
     *                       The thread owned by this mailbox will process this BoundRequest.
     */
    public BoundRequestBase(final Mailbox _targetMailbox) {
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
    public void signal() throws Exception {
        mailbox.signal((_Request<Void, Actor>) this, null);
    }

    @Override
    public void signal(final Mailbox _source) throws Exception {
        _source.signalTo((_Request<Void, Actor>) this, mailbox, null);
    }

    @Override
    public void send(final Mailbox _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        _source.sendTo(this, mailbox, null, _rp);
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return mailbox.call(this, null);
    }

    @Override
    public void processRequest(final Actor _targetActor,
                               final Transport<RESPONSE_TYPE> _transport) throws Exception {
        processRequest(_transport);
    }
}
