package org.agilewiki.pactor;

/**
 * <p>
 * The basic implementation of the Request interface. The application should extend the RequestBase to
 * create the Request implementations which would be used to send to the PActors mailbox for asynchronous
 * processing.
 * </p>
 */
public abstract class UnboundRequestBase<RESPONSE_TYPE, TARGET_ACTOR_TYPE extends Actor>
        implements UnboundRequest<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    @Override
    public void send(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        _targetActor.getMailbox().send((_Request<Void, Actor>) this, _targetActor);
    }

    @Override
    public void send(final Mailbox source, final TARGET_ACTOR_TYPE _targetActor)
            throws Exception {
        _targetActor.getMailbox().send((_Request<Void, Actor>) this, source, _targetActor);
    }

    @Override
    public void reply(final Mailbox source,
                      final TARGET_ACTOR_TYPE _targetActor,
                      final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        _targetActor.getMailbox().reply(this, source, _targetActor, responseProcessor);
    }

    @Override
    public RESPONSE_TYPE pend(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        return (RESPONSE_TYPE) _targetActor.getMailbox().pend(this, _targetActor);
    }
}
