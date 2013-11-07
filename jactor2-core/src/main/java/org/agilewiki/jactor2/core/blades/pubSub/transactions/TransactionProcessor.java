package org.agilewiki.jactor2.core.blades.pubSub.transactions;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class TransactionProcessor<CHANGE_MANAGER extends AutoCloseable, IMMUTABLE_STATE, IMMUTABLE_CHANGES>
        extends BladeBase {
    protected IMMUTABLE_STATE immutableState;
    protected final CommonReactor commonReactor;
    public final RequestBus<IMMUTABLE_CHANGES> validationBus;
    public final RequestBus<IMMUTABLE_CHANGES> changeBus;

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        this(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), _immutableState);
    }

    public TransactionProcessor(final IsolationReactor _isolationReactor,
                                final CommonReactor _commonReactor,
                                final IMMUTABLE_STATE _immutableState) throws Exception {
        initialize(_isolationReactor);
        commonReactor = _commonReactor;
        immutableState = _immutableState;
        validationBus = new RequestBus<IMMUTABLE_CHANGES>(_commonReactor);
        changeBus = new RequestBus<IMMUTABLE_CHANGES>(_commonReactor);
    }

    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    abstract protected CHANGE_MANAGER newChangeManager();

    abstract protected IMMUTABLE_CHANGES newChanges();

    abstract protected void newImmutableState();
}
