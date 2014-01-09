package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.modules.impl.FacilityImpl;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class Facility extends NonBlockingReactor {
    public Facility(final String _name) throws Exception {
        asFacilityImpl().setName(_name);
    }

    public Facility(final String _name, final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize);
        asFacilityImpl().setName(_name);
    }

    public Facility(final String _name, final int _initialOutboxSize, final int _initialLocalQueueSize,
                    final Recovery _recovery, final Scheduler _scheduler)
            throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
        asFacilityImpl().setName(_name);
    }

    protected FacilityImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                             final int _initialOutboxSize, final int _initialLocalQueueSize,
                                             final Recovery _recovery, final Scheduler _scheduler)
            throws Exception {
        return new FacilityImpl(_initialOutboxSize, _initialLocalQueueSize,
                _recovery, _scheduler);
    }

    public FacilityImpl asFacilityImpl() {
        return (FacilityImpl) asReactorImpl();
    }

    public String getName() {
        return asFacilityImpl().getName();
    }

    public PropertiesProcessor getPropertiesProcessor() {
        return asFacilityImpl().getPropertiesProcessor();
    }

    public Object getProperty(final String propertyName) {
        return asFacilityImpl().getProperty(propertyName);
    }

    public AsyncRequest<Void> putPropertyAReq(final String _propertyName,
                                              final Object _expectedValue,
                                              final Object _propertyValue) {
        return asFacilityImpl().putPropertyAReq(_propertyName, _expectedValue, _propertyValue);
    }

    public AsyncRequest<Void> putPropertyAReq(final String _propertyName,
                                              final Object _propertyValue) {
        return asFacilityImpl().putPropertyAReq(_propertyName, _propertyValue);
    }
}
