package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class TransactionProcessor<STATE, STATE_WRAPPER, CHANGES, IMMUTABLE_STATE> extends BladeBase {
    private STATE state;
    private IMMUTABLE_STATE immutableState;
    private final Set<Validator<CHANGES>> validators =
            new HashSet<Validator<CHANGES>>();
    private final Set<ChangeNotificationSubscriber<CHANGES>> changeNotificationSubscribers =
            new HashSet<ChangeNotificationSubscriber<CHANGES>>();

    public TransactionProcessor(final IsolationReactor _isolationReactor, final STATE _initialState) throws Exception {
        initialize(_isolationReactor);
        state = _initialState;
        immutableState = newImmutableState(state);
    }

    abstract protected IMMUTABLE_STATE newImmutableState(STATE _state);

    abstract protected STATE_WRAPPER newStateWrapper(STATE _state);

    abstract protected CHANGES getChanges(STATE_WRAPPER _stateWrapper);

    abstract protected STATE updateState(STATE_WRAPPER _stateWrapper);

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    public SyncRequest<Boolean> addValidatorSReq(
            final Validator<CHANGES> _validator) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return validators.add(_validator);
            }
        };
    }

    public SyncRequest<Boolean> removeValidatorSReq(
            final Validator<CHANGES> _validator) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return validators.remove(_validator);
            }
        };
    }

    public SyncRequest<IMMUTABLE_STATE> addChangeNotificationSubscriberSReq(
            final ChangeNotificationSubscriber<CHANGES> _changeNotificationSubscriber) {
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
            final ChangeNotificationSubscriber<CHANGES> _changeNotificationSubscriber) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return changeNotificationSubscribers.remove(_changeNotificationSubscriber);
            }
        };
    }

    public AsyncRequest<String> processTransaction(final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncBladeRequest<String>() {
            AsyncResponseProcessor<String> dis = this;
            STATE_WRAPPER stateWrapper;
            int validatorsSize;
            int validatorsCount;

            private void changeNotifications() throws Exception {
                immutableState = newImmutableState(state);
                state = updateState(stateWrapper);
                CHANGES changes = getChanges(stateWrapper);
                Iterator<ChangeNotificationSubscriber<CHANGES>> it = changeNotificationSubscribers.iterator();
                while (it.hasNext()) {
                    ChangeNotificationSubscriber<CHANGES> changeNotificationSubscriber = it.next();
                    changeNotificationSubscriber.changeNotificationAReq(changes).signal();
                }
                dis.processAsyncResponse(null);
            }

            AsyncResponseProcessor<String> validatorsResponseProcessor = new AsyncResponseProcessor<String>() {
                @Override
                public void processAsyncResponse(String _error) throws Exception {
                    validatorsCount--;
                    if (_error != null)
                        dis.processAsyncResponse(_error);
                    else if (validatorsCount == validatorsSize)
                        changeNotifications();
                }
            };

            AsyncResponseProcessor<Void> updateResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    validatorsSize = validators.size();
                    if (validatorsSize == 0) {
                        changeNotifications();
                        return;
                    }
                    CHANGES changes = getChanges(stateWrapper);
                    Iterator<Validator<CHANGES>> it = validators.iterator();
                    while (it.hasNext()) {
                        Validator<CHANGES> validator = it.next();
                        send(validator.validateAReq(changes), validatorsResponseProcessor);
                    }
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                stateWrapper = newStateWrapper(state);
                send(_transaction.updateAReq(stateWrapper), updateResponseProcessor);
            }
        };
    }
}
