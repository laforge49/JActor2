package org.agilewiki.pactor.api;

/**
 * UnboundRequestBase is typically subclassed to create requests that are targeted to a class
 * of actors or to an interface, rather than to a specific instance. The target class must however
 * implement the Actor interface.
 * <p/>
 * <pre>
 * public interface DudActor extends Actor {
 *     public String getDuddly();
 * }
 *
 * public class DuddlyReq extends UnboundRequestBase&lt;String, DudActor&gt; {
 *     public void processRequest(final DudActor _targetActor, final ResponseProcessor&lt;String response&gt; _rp)
 *             throws Exception {
 *         _rp.processResponse(_targetActor.getDuddly());
 *     }
 * }
 * </pre>
 *
 * @param <RESPONSE_TYPE>     The class of the result returned when this UnboundRequest is processed.
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be used when this UnboundRequest is processed.
 */
public abstract class UnboundRequestBase<RESPONSE_TYPE, TARGET_ACTOR_TYPE extends Actor>
        implements UnboundRequest<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    @Override
    public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        _targetActor.getMailbox().signal((_Request<Void, Actor>) this,
                _targetActor);
    }

    @Override
    public void signal(final Mailbox _source,
                       final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        _targetActor.getMailbox().signal((_Request<Void, Actor>) this, _source,
                _targetActor);
    }

    @Override
    public void send(final Mailbox _source,
                     final TARGET_ACTOR_TYPE _targetActor,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        _targetActor.getMailbox().send(this, _source, _targetActor, _rp);
    }

    @Override
    public RESPONSE_TYPE call(final TARGET_ACTOR_TYPE _targetActor)
            throws Exception {
        return _targetActor.getMailbox().call(this,
                _targetActor);
    }
}
