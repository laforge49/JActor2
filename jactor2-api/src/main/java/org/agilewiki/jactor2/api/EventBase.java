package org.agilewiki.jactor2.api;

/**
 * EventBase is typically subclassed to create requests that are targeted to a class
 * of actors or to an interface, rather than to a specific instance. The target class must however
 * implement the Actor interface.
 * <p/>
 * <pre>
 * public interface DudActor extends Actor {
 *     public String getDuddly();
 * }
 *
 * public class DuddlyReq extends EventBase&lt;String, DudActor&gt; {
 *     public void processRequest(final DudActor _targetActor, final ResponseProcessor&lt;String response&gt; _rp)
 *             throws Exception {
 *         _rp.processResponse(_targetActor.getDuddly());
 *     }
 * }
 * </pre>
 *
 * @param <RESPONSE_TYPE>     The class of the result returned when this Event is processed.
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be used when this Event is processed.
 */
public abstract class EventBase<RESPONSE_TYPE, TARGET_ACTOR_TYPE extends Actor>
        implements Event<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    @Override
    public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        _targetActor.getMailbox().signal((_Request<Void, Actor>) this,
                _targetActor);
    }

    @Override
    public void send(final Mailbox _source,
                     final TARGET_ACTOR_TYPE _targetActor,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        _source.sendTo(this, _targetActor.getMailbox(), _targetActor, _rp);
    }
}
