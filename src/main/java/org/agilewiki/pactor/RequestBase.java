package org.agilewiki.pactor;

/**
 * Request objects are typically created as an anonymous class within the targeted Actor and bound
 * to that actor's mailbox. By this means the request can update an actor's state in a thread-safe way.
 * <p>
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
    public void signal() throws Exception {
        mailbox.signal((_Request<Void, Actor>) this, null);
    }

    @Override
    public void signal(final Mailbox _source) throws Exception {
        mailbox.signal((_Request<Void, Actor>) this, _source, null);
    }

    @Override
    public void send(final Mailbox _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp)
            throws Exception {
        mailbox.send(this, _source, null, _rp);
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return mailbox.call(this, null);
    }

    @Override
    public void processRequest(final Actor _targetActor,
                               final ResponseProcessor<RESPONSE_TYPE> _rp)
            throws Exception {
        processRequest(_rp);
    }
}

/*
public class Actor1 {
    private final Mailbox mailbox;
    public final Request<String> hi1;

    public Actor1(final Mailbox mbox) {
        this.mailbox = mbox;

        hi1 = new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<String> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse("Hello world!");
            }
        };
    }
}
*/