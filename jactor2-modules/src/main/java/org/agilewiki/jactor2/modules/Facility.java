package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.util.Closer;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.modules.impl.FacilityImpl;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class Facility extends NonBlockingReactor {
    public Facility() throws Exception {
    }

    public Facility(int _initialOutboxSize, int _initialLocalQueueSize) throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize);
    }

    public Facility(int _initialOutboxSize, int _initialLocalQueueSize, Recovery _recovery, Scheduler _scheduler)
            throws Exception {
        super(Plant.getReactor().asReactorImpl(), _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
    }

    protected NonBlockingReactorImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                                       final int _initialOutboxSize, final int _initialLocalQueueSize,
                                                       final Recovery _recovery, final Scheduler _scheduler)
            throws Exception {
        return new NonBlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize,
                _recovery, _scheduler);
    }

    FacilityImpl asFacilityImpl() {
        return (FacilityImpl) asReactorImpl();
    };

    String getName() {
        return asFacilityImpl().getName();
    };

    PropertiesProcessor getPropertiesProcessor() {
        return asFacilityImpl().getPropertiesProcessor();
    }
}
