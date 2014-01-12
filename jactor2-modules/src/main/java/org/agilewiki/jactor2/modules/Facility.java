package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.util.Recovery;
import org.agilewiki.jactor2.modules.impl.FacilityImpl;
import org.agilewiki.jactor2.modules.impl.MPlantImpl;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class Facility extends NonBlockingReactor {
    public static AsyncRequest<Facility> createFacilityAReq(final String _name) throws Exception {
        MPlantImpl plantImpl = MPlantImpl.getSingleton();
        final int initialBufferSize;
        Integer v = (Integer) plantImpl.getProperty(MPlantImpl.initialBufferSizeKey(_name));
        if (v != null)
            initialBufferSize = v;
        else
            initialBufferSize = plantImpl.getInternalFacility().asFacilityImpl().getInitialBufferSize();
        final int initialLocalQueueSize;
        v = (Integer) plantImpl.getProperty(MPlantImpl.initialLocalMessageQueueSizeKey(_name));
        if (v != null)
            initialLocalQueueSize = v;
        else
            initialLocalQueueSize = plantImpl.getInternalFacility().asFacilityImpl().getInitialLocalQueueSize();
        final Facility facility = new Facility(initialBufferSize, initialLocalQueueSize);
        facility.asFacilityImpl().setName(_name);
        return new AsyncRequest<Facility>(facility) {
            @Override
            public void processAsyncRequest() throws Exception {
                send(facility.asFacilityImpl().startFacilityAReq(), this, facility);
            }
        };
    }

    public Facility() throws Exception {
    }

    private Facility(final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        super(_initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    protected FacilityImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                             final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        return new FacilityImpl(_initialOutboxSize, _initialLocalQueueSize);
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
