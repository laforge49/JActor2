package org.agilewiki.jactor2.api;

public class EventMessage extends Message {
    protected final Actor targetActor;
    protected final Event event;

    public <E, A extends Actor> EventMessage(final A _targetActor, final Event _event) {
        targetActor = _targetActor;
        event = _event;
    }

    @Override
    public boolean isForeign() {
        return false;
    }

    @Override
    public boolean isResponsePending() {
        return false;
    }

    public final void event()
            throws Exception {
        targetActor.getMailbox().unbufferedAddMessages(this, false);
    }

    @Override
    public void close() throws Exception {
    }

    public void eval(final Mailbox _targetMailbox) {
        _targetMailbox.setExceptionHandler(null);
        _targetMailbox.setCurrentMessage(this);
        try {
            event.processEvent(targetActor);
        } catch (final Throwable t) {
            processThrowable(_targetMailbox, t);
        }
    }

    protected void processThrowable(final Mailbox _activeMailbox, final Throwable _t) {
        ExceptionHandler exceptionHandler = _activeMailbox.getExceptionHandler();
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(_t);
            } catch (final Throwable u) {
                _activeMailbox.getLogger().error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), u);
                _activeMailbox.getLogger().error("Thrown by exception handler and uncaught "
                        + exceptionHandler.getClass().getName(), _t);
            }
        }
    }
}
