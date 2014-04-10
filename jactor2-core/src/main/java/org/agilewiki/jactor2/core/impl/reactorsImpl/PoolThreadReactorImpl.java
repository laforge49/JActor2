package org.agilewiki.jactor2.core.impl.reactorsImpl;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantBaseImpl;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
