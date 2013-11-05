package org.agilewiki.jactor2.core.blades.oldTransactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.oldRequestBus.RequestBus;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class TransactionProcessor<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES extends ImmutableChanges, IMMUTABLE_STATE>
        extends BladeBase {
    protected IMMUTABLE_STATE immutableState;
    private final CommonReactor commonReactor;
    final private ValidationBus<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> validationBus;
    final private RequestBus<IMMUTABLE_CHANGES, Void> changeBus;

    public TransactionProcessor(final IsolationReactor _isolationReactor,
            final IMMUTABLE_STATE _immutableState) throws Exception {
        this(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), _immutableState);
    }

    public TransactionProcessor(final IsolationReactor _isolationReactor,
            final NonBlockingReactor _commonReactor,
            final IMMUTABLE_STATE _immutableState) throws Exception {
        initialize(_isolationReactor);
        commonReactor = _commonReactor;
        immutableState = _immutableState;
        validationBus = new ValidationBus<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>(
                _commonReactor);
        changeBus = new RequestBus<IMMUTABLE_CHANGES, Void>(_commonReactor);
    }

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    public AsyncRequest<ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>> addValidatorAReq(
            final Validator<IMMUTABLE_CHANGES> _validator) {
        return new AsyncBladeRequest<ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>>() {
            AsyncResponseProcessor<ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> subscription = new ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>(
                        _validator, validationBus);
                send(subscription.subscribeAReq(),
                        new AsyncResponseProcessor<Boolean>() {
                            @Override
                            public void processAsyncResponse(
                                    final Boolean _response) throws Exception {
                                dis.processAsyncResponse(_response ? subscription
                                        : null);
                            }
                        });
            }
        };
    }

    public AsyncRequest<ChangeSubscription<IMMUTABLE_CHANGES>> addChangeNotificationSubscriberAReq(
            final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> _changeNotificationSubscriber) {
        return new AsyncBladeRequest<ChangeSubscription<IMMUTABLE_CHANGES>>() {
            AsyncResponseProcessor<ChangeSubscription<IMMUTABLE_CHANGES>> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final ChangeSubscription<IMMUTABLE_CHANGES> subscription = new ChangeSubscription<IMMUTABLE_CHANGES>(
                        _changeNotificationSubscriber, changeBus);
                send(subscription.subscribeAReq(),
                        new AsyncResponseProcessor<Boolean>() {
                            @Override
                            public void processAsyncResponse(
                                    final Boolean _response) throws Exception {
                                dis.processAsyncResponse(_response ? subscription
                                        : null);
                            }
                        });
            }
        };
    }

    abstract protected void newImmutableState();

    abstract protected STATE_WRAPPER newStateWrapper();

    abstract protected IMMUTABLE_CHANGES newChanges();

    public AsyncRequest<Void> processTransactionAReq(
            final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncBladeRequest<Void>() {

            @Override
            protected void processAsyncRequest() throws Exception {
                send(ptAReq(_transaction), this);
            }
        };
    }

    private AsyncRequest<Void> ptAReq(
            final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncRequest<Void>(commonReactor) {
            AsyncResponseProcessor<Void> dis = this;
            STATE_WRAPPER stateWrapper;
            IMMUTABLE_CHANGES changes;

            AsyncResponseProcessor<String> validatorsResponseProcessor = new AsyncResponseProcessor<String>() {
                @Override
                public void processAsyncResponse(final String _error)
                        throws Exception {
                    if (_error != null) {
                        throw new IllegalArgumentException(_error);
                    } else {
                        newImmutableState();
                        send(changeBus.signalSReq(changes), dis, null);
                    }
                }
            };

            AsyncResponseProcessor<Void> updateResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    stateWrapper.close();
                    changes = newChanges();
                    send(validationBus.sendAReq(changes),
                            validatorsResponseProcessor);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                stateWrapper = newStateWrapper();
                send(_transaction.updateAReq(stateWrapper),
                        updateResponseProcessor);
            }
        };
    }
}
