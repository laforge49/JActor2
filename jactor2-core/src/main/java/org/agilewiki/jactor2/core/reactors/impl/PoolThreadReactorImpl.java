package org.agilewiki.jactor2.core.reactors.impl;

/**
 * Common code for BlockingReactor, NonBlockingReactor and IsolationReactor, which are not bound to a thread.
 * <p>
 * PoolThreadReactorImpl supports thread migration only between instances of this class.
 * </p>
 */
public interface PoolThreadReactorImpl extends ReactorImpl {
    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    public Runnable getOnIdle();

    public void setOnIdle(Runnable onIdle);
}
