package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class Plant {

    public static Plant getSingleton() {
        return PlantImpl.getSingleton();
    }

    private final PlantImpl plantImpl;

    public Plant() throws Exception {
        plantImpl = new PlantImpl(this);
    }

    public Plant(final int _threadCount) throws Exception {
        plantImpl = new PlantImpl(this, _threadCount);
    }

    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        plantImpl = new PlantImpl(this, _plantConfiguration);
    }

    public PlantImpl getPlantImpl() {
        return plantImpl;
    }

    public Facility facility() {
        return plantImpl;
    }

    public void close() throws Exception {
        plantImpl.close();
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return plantImpl.createFacilityAReq(_name);
    }

    public AsyncRequest<Void> activatorPropertyAReq(final String _facilityName, final String _className) {
        return plantImpl.activatorPropertyAReq(_facilityName, _className);
    }

    public AsyncRequest<Void> autoStartAReq(final String _facilityName, final boolean _newValue) {
        return plantImpl.autoStartAReq(_facilityName, _newValue);
    }

    public AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return plantImpl.dependencyPropertyAReq(_dependentName, _dependencyName);
    }

    public AsyncRequest<Void> purgeFacilitySReq(final String _facilityName) {
        return plantImpl.purgeFacilitySReq(_facilityName);
    }

    public AsyncRequest<Void> failedAReq(final String _facilityName, final Object _newValue) {
        return plantImpl.failedAReq(_facilityName, _newValue);
    }

    public void stopFacility(final String _facilityName) throws Exception {
        plantImpl.stopFacility(_facilityName);
    }

    public AsyncRequest<Void> stoppedAReq(final String _facilityName, final boolean _newValue) {
        return plantImpl.stoppedAReq(_facilityName, _newValue);
    }
}
