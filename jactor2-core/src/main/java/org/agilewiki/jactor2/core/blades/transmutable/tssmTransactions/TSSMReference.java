package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.Transaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.TransmutableReference;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

import java.util.Map;
import java.util.SortedMap;

/**
 * Supports validation and notifications of changes to a TSSMap.
 */
public class TSSMReference<VALUE> extends TransmutableReference<SortedMap<String, VALUE>, TSSMap<VALUE>> {

    /**
     * The RequestBus used to validate the changes made by a transaction.
     */
    public final RequestBus<TSSMChanges<VALUE>> validationBus;

    /**
     * The RequestBus used to signal the changes made by a validated transaction.
     */
    public final RequestBus<TSSMChanges<VALUE>> changeBus;

    public TSSMReference() throws Exception {
        this(new TSSMap<VALUE>());
    }

    public TSSMReference(IsolationReactor _reactor) throws Exception {
        this(new TSSMap<VALUE>(), _reactor);
    }

    public TSSMReference(NonBlockingReactor _parentReactor) throws Exception {
        this(new TSSMap<VALUE>(), _parentReactor);
    }

    public TSSMReference(Map<String, VALUE> _map) throws Exception {
        this(new TSSMap<VALUE>(_map));
    }

    public TSSMReference(Map<String, VALUE> _map, IsolationReactor _reactor) throws Exception {
        this(new TSSMap<VALUE>(_map), _reactor);
    }

    public TSSMReference(Map<String, VALUE> _map, NonBlockingReactor _parentReactor) throws Exception {
        this(new TSSMap<VALUE>(_map), _parentReactor);
    }

    public TSSMReference(SortedMap<String, VALUE> _sortedMap) throws Exception {
        this(new TSSMap<VALUE>(_sortedMap));
    }

    public TSSMReference(SortedMap<String, VALUE> _sortedMap, IsolationReactor _reactor) throws Exception {
        this(new TSSMap<VALUE>(_sortedMap), _reactor);
    }

    public TSSMReference(SortedMap<String, VALUE> _sortedMap, NonBlockingReactor _parentReactor) throws Exception {
        this(new TSSMap<VALUE>(_sortedMap), _parentReactor);
    }

    private TSSMReference(TSSMap<VALUE> _tssMap) throws Exception {
        super(_tssMap);
        final NonBlockingReactor parentReactor = (NonBlockingReactor) getReactor()
                .getParentReactor();
        validationBus = new RequestBus<TSSMChanges<VALUE>>(parentReactor);
        changeBus = new RequestBus<TSSMChanges<VALUE>>(parentReactor);
    }

    private TSSMReference(TSSMap<VALUE> _tssMap, IsolationReactor _reactor) throws Exception {
        super(_tssMap, _reactor);
        final NonBlockingReactor parentReactor = (NonBlockingReactor) getReactor()
                .getParentReactor();
        validationBus = new RequestBus<TSSMChanges<VALUE>>(parentReactor);
        changeBus = new RequestBus<TSSMChanges<VALUE>>(parentReactor);
    }

    private TSSMReference(TSSMap<VALUE> _tssMap, NonBlockingReactor _parentReactor) throws Exception {
        super(_tssMap, _parentReactor);
        validationBus = new RequestBus<TSSMChanges<VALUE>>(_parentReactor);
        changeBus = new RequestBus<TSSMChanges<VALUE>>(_parentReactor);
    }

    @Override
    public AOp<Void> applyAOp(final Transaction<SortedMap<String, VALUE>, TSSMap<VALUE>> _tssmTransaction) {
        return new AOp<Void>("apply", getReactor()) {

            private TSSMChanges<VALUE> tssmChanges;

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                final TSSMTransaction<VALUE> tssmTransaction = (TSSMTransaction<VALUE>) _tssmTransaction;

                final AsyncResponseProcessor<Void> validationResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        tssmTransaction.tssmChangeManager.close();
                        updateUnmodifiable();
                        _asyncRequestImpl.send(changeBus
                                        .sendsContentAOp(tssmChanges),
                                _asyncResponseProcessor, getTransmutable());
                    }
                };

                final AsyncResponseProcessor<Void> superResponseProcessor =
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(final Void _response)
                                    throws Exception {
                                tssmChanges = new TSSMChanges<VALUE>(
                                        tssmTransaction.tssmChangeManager);
                                _asyncRequestImpl.send(validationBus
                                                .sendsContentAOp(tssmChanges),
                                        validationResponseProcessor);
                            }
                        };

                _tssmTransaction._eval(TSSMReference.this, _asyncRequestImpl, superResponseProcessor);
            }
        };
    }
}
