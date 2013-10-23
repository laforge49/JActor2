package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class TransactionProcessor<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES, IMMUTABLE_STATE> extends BladeBase {
    protected IMMUTABLE_STATE immutableState;
    private final Set<Validator<IMMUTABLE_CHANGES>> validators =
            new HashSet<Validator<IMMUTABLE_CHANGES>>();
    private final Set<ChangeNotificationSubscriber<IMMUTABLE_CHANGES>> changeNotificationSubscribers =
            new HashSet<ChangeNotificationSubscriber<IMMUTABLE_CHANGES>>();

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        initialize(_isolationReactor);
        immutableState = _immutableState;
    }

    abstract protected void newImmutableState();

    abstract protected STATE_WRAPPER newStateWrapper();

    abstract protected IMMUTABLE_CHANGES newChanges();

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    public SyncRequest<Boolean> addValidatorSReq(
            final Validator<IMMUTABLE_CHANGES> _validator) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return validators.add(_validator);
            }
        };
    }

    public SyncRequest<Boolean> removeValidatorSReq(
            final Validator<IMMUTABLE_CHANGES> _validator) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return validators.remove(_validator);
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

    public AsyncRequest<String> processTransaction(final Transaction<STATE_WRAPPER> _transaction) throws Exception {
        return new AsyncBladeRequest<String>() {
            AsyncResponseProcessor<String> dis = this;
            STATE_WRAPPER stateWrapper;
            IMMUTABLE_CHANGES changes;
            int validatorsSize;
            int validatorsCount;

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
                    stateWrapper.close();
                    changes = newChanges();
                    validatorsSize = validators.size();
                    if (validatorsSize == 0) {
                        changeNotifications();
                        return;
                    }
                    Iterator<Validator<IMMUTABLE_CHANGES>> it = validators.iterator();
                    while (it.hasNext()) {
                        Validator<IMMUTABLE_CHANGES> validator = it.next();
                        send(validator.validateAReq(changes), validatorsResponseProcessor);
                    }
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
