package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
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
    final private ValidationBus<IMMUTABLE_CHANGES> validationBus;
    private final Set<ChangeNotificationSubscriber<IMMUTABLE_CHANGES>> changeNotificationSubscribers =
            new HashSet<ChangeNotificationSubscriber<IMMUTABLE_CHANGES>>();

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        initialize(_isolationReactor);
        immutableState = _immutableState;
        validationBus = new ValidationBus<IMMUTABLE_CHANGES>(new NonBlockingReactor(_isolationReactor.getFacility()));
    }

    abstract protected void newImmutableState();

    abstract protected STATE_WRAPPER newStateWrapper();

    abstract protected IMMUTABLE_CHANGES newChanges();

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    public AsyncRequest<ValidationSubscription<IMMUTABLE_CHANGES>> addValidatorSReq(
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

    public AsyncRequest<Boolean> removeValidatorSReq(
            final ValidationSubscription<IMMUTABLE_CHANGES> _subscription) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncResponseProcessor<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                send(_subscription.unsubscribeAReq(), dis);
            }
        };
    }

    public SyncRequest<IMMUTABLE_STATE> addChangeNotificationSubscriberSReq(
            final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> _changeNotificationSubscriber) {
        return new SyncBladeRequest<IMMUTABLE_STATE>() {
            @Override
            protected IMMUTABLE_STATE processSyncRequest() throws Exception {
                if (changeNotificationSubscribers.add(_changeNotificationSubscriber))
                    return immutableState;
                else
                    return null;
            }
        };
    }

    public SyncRequest<Boolean> removeChangeNotificationSubscriberSReq(
            final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> _changeNotificationSubscriber) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return changeNotificationSubscribers.remove(_changeNotificationSubscriber);
            }
        };
    }

    public AsyncRequest<String> processTransactionAReq(final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncBladeRequest<String>() {
            AsyncResponseProcessor<String> dis = this;
            STATE_WRAPPER stateWrapper;
            IMMUTABLE_CHANGES changes;

            private void changeNotifications() throws Exception {
                newImmutableState();
                Iterator<ChangeNotificationSubscriber<IMMUTABLE_CHANGES>> it = changeNotificationSubscribers.iterator();
                while (it.hasNext()) {
                    ChangeNotificationSubscriber<IMMUTABLE_CHANGES> changeNotificationSubscriber = it.next();
                    changeNotificationSubscriber.changeNotificationAReq(changes).signal();
                }
                dis.processAsyncResponse(null);
            }

            AsyncResponseProcessor<String> validatorsResponseProcessor = new AsyncResponseProcessor<String>() {
                @Override
                public void processAsyncResponse(String _error) throws Exception {
                    if (_error != null)
                        dis.processAsyncResponse(_error);
                    else changeNotifications();
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
