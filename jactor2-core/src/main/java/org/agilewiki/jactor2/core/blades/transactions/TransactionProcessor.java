package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * The TransactionProcessor blade uses 2 reactors, an IsolationReactor to ensure transaction isolation,
 * and a CommonReactor for transaction processing and for use by the validation and change RequesBus instances.
 *
 * @param <CHANGE_MANAGER>       Used when processing a transaction to update the state.
 * @param <IMMUTABLE_STATE>      The type of state.
 * @param <IMMUTABLE_CHANGES>    The transaction changes passed to the subscribers of the validation and
 *                           change RequestBus instances.
 */
abstract public class TransactionProcessor<CHANGE_MANAGER extends AutoCloseable, IMMUTABLE_STATE, IMMUTABLE_CHANGES>
        extends IsolationBladeBase {

    /**
     * The state as of the completion of the last successful transaction.
     */
    protected IMMUTABLE_STATE immutableState;

    /**
     * The reactor used for transaction processing and by the two RequestBus instances.
     */
    public final CommonReactor commonReactor;

    /**
     * The RequestBus used to validate the changes made by a transaction.
     */
    public final RequestBus<IMMUTABLE_CHANGES> validationBus;

    /**
     * The RequestBus used to signal the changes made by a validated transaction.
     */
    public final RequestBus<IMMUTABLE_CHANGES> changeBus;

    /**
     * Create a transaction processor.
     * The CommonReactor used by the transaction processor is created using the
     * isolation reactor's facility.
     *
     * @param _isolationReactor    The IsolationReactor used to isolate transactions.
     * @param _initialState        The initial state to be used.
     */
    protected TransactionProcessor(final IsolationReactor _isolationReactor,
                                final IMMUTABLE_STATE _initialState) throws Exception {
        this(_isolationReactor, new NonBlockingReactor(
                _isolationReactor.getFacility()), _initialState);
    }

    /**
     * Create a transaction processor.
     *
     * @param _isolationReactor    The IsolationReactor used to isolate transactions.
     * @param _commonReactor       The reactor used for transaction processing and by the two RequestBus instances.
     * @param _initialState        The initial state to be used.
     */
    protected TransactionProcessor(final IsolationReactor _isolationReactor,
                                final NonBlockingReactor _commonReactor,
                                final IMMUTABLE_STATE _initialState) throws Exception {
        initialize(_isolationReactor);
        commonReactor = _commonReactor;
        immutableState = _initialState;
        validationBus = new RequestBus<IMMUTABLE_CHANGES>(_commonReactor);
        changeBus = new RequestBus<IMMUTABLE_CHANGES>(_commonReactor);
    }

    /**
     * Returns the state as of the completion of the last successful transaction.
     *
     * @return The state as of the completion of the last successful transaction.
     */
    public IMMUTABLE_STATE getImmutableState() {
        return immutableState;
    }

    /**
     * Creates the change manager to be used to manage the updates of a single transaction.
     *
     * @return The change manager.
     */
    abstract protected CHANGE_MANAGER newChangeManager();

    /**
     * Creates the immutable changes to be used to validate and signal the changes made by the transaction.
     *
     * @return The immutable changes.
     */
    abstract protected IMMUTABLE_CHANGES newChanges();

    /**
     * Update the current state after a transaction has been validated.
     */
    abstract protected void newImmutableState();
}
