package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.requestBus.RequestBus;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class TransactionProcessor
        <STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES, IMMUTABLE_STATE> extends BladeBase {
    protected IMMUTABLE_STATE immutableState;
    private final NonBlockingReactor nonBlockingReactor;
    final private ValidationBus<IMMUTABLE_CHANGES> validationBus;
    final private RequestBus<IMMUTABLE_CHANGES, Void> changeBus;

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        this(_isolationReactor, new NonBlockingReactor(_isolationReactor.getFacility()), _immutableState);
    }

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final NonBlockingReactor _nonBlockingReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        initialize(_isolationReactor);
        nonBlockingReactor = _nonBlockingReactor;
        immutableState = _immutableState;
        validationBus = new ValidationBus<IMMUTABLE_CHANGES>(_nonBlockingReactor);
        changeBus = new RequestBus<IMMUTABLE_CHANGES, Void>(_nonBlockingReactor);
    }

    abstract protected void newImmutableState();

    abstract protected STATE_WRAPPER newStateWrapper();

    abstract protected IMMUTABLE_CHANGES newChanges();

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    public AsyncRequest<ValidationSubscription<IMMUTABLE_CHANGES>> addValidatorAReq(
            final Validator<IMMUTABLE_CHANGES> _validator) {
        return new AsyncBladeRequest<ValidationSubscription<IMMUTABLE_CHANGES>>() {
            AsyncResponseProcessor<ValidationSubscription<IMMUTABLE_CHANGES>> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final ValidationSubscription<IMMUTABLE_CHANGES> subscription =
                        new ValidationSubscription<IMMUTABLE_CHANGES>(_validator, validationBus);
                send(subscription.subscribeAReq(), new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(Boolean _response) throws Exception {
                        dis.processAsyncResponse(_response ? subscription : null);
                    }
                });
            }
        };
    }

    public AsyncRequest<Boolean> removeValidatorAReq(
            final ValidationSubscription<IMMUTABLE_CHANGES> _subscription) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncResponseProcessor<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                send(_subscription.unsubscribeAReq(), dis);
            }
        };
    }

    public AsyncRequest<ChangeSubscription<IMMUTABLE_CHANGES>> addChangeNotificationSubscriberAReq(
            final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> _changeNotificationSubscriber) {
        return new AsyncBladeRequest<ChangeSubscription<IMMUTABLE_CHANGES>>() {
            AsyncResponseProcessor<ChangeSubscription<IMMUTABLE_CHANGES>> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final ChangeSubscription<IMMUTABLE_CHANGES> subscription =
                        new ChangeSubscription<IMMUTABLE_CHANGES>(_changeNotificationSubscriber, changeBus);
                send(subscription.subscribeAReq(), new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(Boolean _response) throws Exception {
                        dis.processAsyncResponse(_response ? subscription : null);
                    }
                });
            }
        };
    }

    public AsyncRequest<Boolean> removeChangeNotificationSubscriberAReq(
            final ChangeSubscription<IMMUTABLE_CHANGES> _subscription) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncResponseProcessor<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                send(_subscription.unsubscribeAReq(), dis);
            }
        };
    }

    public AsyncRequest<String> processTransactionAReq(final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncBladeRequest<String>() {

            @Override
            protected void processAsyncRequest() throws Exception {
                send(ptAReq(_transaction), this);
            }
        };
    }

    private AsyncRequest<String> ptAReq(final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncRequest<String>(nonBlockingReactor) {
            AsyncResponseProcessor<String> dis = this;
            STATE_WRAPPER stateWrapper;
            IMMUTABLE_CHANGES changes;

            AsyncResponseProcessor<String> validatorsResponseProcessor = new AsyncResponseProcessor<String>() {
                @Override
                public void processAsyncResponse(String _error) throws Exception {
                    if (_error != null)
                        dis.processAsyncResponse(_error);
                    else {
                        newImmutableState();
                        send(changeBus.signalSReq(changes), new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(Void _response) throws Exception {
                                dis.processAsyncResponse(null);
                            }
                        });
                    }
                }
            };

            AsyncResponseProcessor<Void> updateResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    stateWrapper.close();
                    changes = newChanges();
                    send(validationBus.sendAReq(changes), validatorsResponseProcessor);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                stateWrapper = newStateWrapper();
                send(_transaction.updateAReq(stateWrapper), updateResponseProcessor);
            }
        };
    }
}
