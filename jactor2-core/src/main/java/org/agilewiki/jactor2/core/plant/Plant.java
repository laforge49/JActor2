package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class Plant {
    private final PlantImpl impl;

    public Plant() throws Exception {
        impl = new PlantImpl(this);
    }

    public Plant(final int _threadCount) throws Exception {
        impl = new PlantImpl(this, _threadCount);
    }

    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        impl = new PlantImpl(this, _plantConfiguration);
    }

    public PlantImpl impl() {
        return impl;
    }

    public Facility facility() {
        return impl;
    }

    public void close() throws Exception {
        impl.close();
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return impl.createFacilityAReq(_name);
    }

    public AsyncRequest<Void> activatorPropertyAReq(final String _facilityName, final String _className) {
        return impl.activatorPropertyAReq(_facilityName, _className);
    }

    public AsyncRequest<Void> autoStartAReq(final String _facilityName, final boolean _newValue) {
        return impl.autoStartAReq(_facilityName, _newValue);
    }

    public AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return impl.dependencyPropertyAReq(_dependentName, _dependencyName);
    }

    public AsyncRequest<Void> purgeFacilitySReq(final String _facilityName) {
        return impl.purgeFacilitySReq(_facilityName);
    }

    public AsyncRequest<Void> failedAReq(final String _facilityName, final Object _newValue) {
        return impl.failedAReq(_facilityName, _newValue);
    }

    public void stopFacility(final String _facilityName) throws Exception {
        impl.stopFacility(_facilityName);
    }

    public AsyncRequest<Void> stoppedAReq(final String _facilityName, final boolean _newValue) {
        return impl.stoppedAReq(_facilityName, _newValue);
    }
}
