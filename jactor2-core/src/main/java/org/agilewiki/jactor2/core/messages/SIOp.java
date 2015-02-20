package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * A SOP with support for signals, which are processed immediately.
 */
public abstract class SIOp<RESPONSE_TYPE> extends SOp<RESPONSE_TYPE> {

    /**
     * Create an asynchronous immediate operation.
     *
     * @param _opName           The name of the operation.
     * @param _targetReactor    The reactor whose thread will process the operation.
     */
    public SIOp(final String _opName, final Reactor _targetReactor) {
        super(_opName, _targetReactor);
    }

    public void signal() {
        PlantImpl.getSingleton().createSyncRequestImpl(this, targetReactor)
                .signal();
    }
}
