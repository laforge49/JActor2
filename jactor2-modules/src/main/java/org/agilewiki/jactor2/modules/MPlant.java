package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.modules.impl.MPlantImpl;

public class MPlant {

    public static Facility getInternalFacility() {
        return MPlantImpl.getSingleton().getInternalFacility();
    }

    public static Facility getFacility(String name) {
        return MPlantImpl.getSingleton().getFacilityImpl(name).asFacility();
    }

    public static AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return Facility.createFacilityAReq(_name);
    }

    public static String getActivatorClassName(final String _facilityName) {
        return MPlantImpl.getSingleton().getActivatorClassName(_facilityName);
    }

    public static AsyncRequest<Void> activatorPropertyAReq(final String _facilityName, final String _className) {
        return MPlantImpl.getSingleton().activatorPropertyAReq(_facilityName, _className);
    }

    public static boolean isAutoStart(String name) {
        return MPlantImpl.getSingleton().isAutoStart(name);
    }

    public static AsyncRequest<Void> autoStartAReq(final String _facilityName, final boolean _newValue) {
        return MPlantImpl.getSingleton().autoStartAReq(_facilityName, _newValue);
    }

    public static AsyncRequest<Void> dependencyPropertyAReq(final String _dependentName, final String _dependencyName) {
        return MPlantImpl.getSingleton().dependencyPropertyAReq(_dependentName, _dependencyName);
    }

    public static AsyncRequest<Void> purgeFacilitySReq(final String _facilityName) {
        return MPlantImpl.getSingleton().purgeFacilitySReq(_facilityName);
    }

    public static Object getFailed(String name) {
        return MPlantImpl.getSingleton().getFailed(name);
    }

    public static void failFacility(final String _facilityName, final Object reason) throws Exception {
        MPlantImpl.getSingleton().failFacility(_facilityName, reason);
    }

    public static AsyncRequest<Void> clearFailedAReq(final String _facilityName) {
        return MPlantImpl.getSingleton().failedAReq(_facilityName, null);
    }

    public static boolean isStopped(String name) {
        return MPlantImpl.getSingleton().isStopped(name);
    }

    public static void stopFacility(final String _facilityName) throws Exception {
        MPlantImpl.getSingleton().stopFacility(_facilityName);
    }

    public static AsyncRequest<Void> clearStoppedAReq(final String _facilityName) {
        return MPlantImpl.getSingleton().stoppedAReq(_facilityName, false);
    }

    public static AsyncRequest<Void> initialLocalMerssageQueueSizePropertyAReq(final String _facilityName, final Integer _value) {
        return MPlantImpl.getSingleton().initialLocalMerssageQueueSizePropertyAReq(_facilityName, _value);
    }

    public static AsyncRequest<Void> initialBufferSizePropertyAReq(final String _facilityName, final Integer _value) {
        return MPlantImpl.getSingleton().initialBufferSizePropertyAReq(_facilityName, _value);
    }

    public MPlant() throws Exception {
        new MPlantImpl();
    }

    public MPlant(final int _threadCount) throws Exception {
        new MPlantImpl(_threadCount);
    }

    public MPlant(final PlantConfiguration _plantConfiguration) throws Exception {
        new MPlantImpl(_plantConfiguration);
    }
}
