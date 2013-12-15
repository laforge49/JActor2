package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.util.Recovery;

public class Plant implements BasicPlant {

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

    public BasicPlant asBasicPlant() {
        return this;
    }

    @Override
    public Plant asPlant() {
        return this;
    }

    @Override
    public Facility asFacility() {
        return plantImpl;
    }

    @Override
    public PlantImpl asPlantImpl() {
        return plantImpl;
    }

    @Override
    public boolean startedClosing() {
        return plantImpl.startedClosing();
    }

    @Override
    public void close() throws Exception {
        plantImpl.close();
    }

    @Override
    public boolean isExitOnClose() {
        return plantImpl.isExitOnClose();
    }

    @Override
    public void exit() {
        plantImpl.exit();
    }

    @Override
    public boolean isForcedExit() {
        return plantImpl.isForcedExit();
    }

    @Override
    public void forceExit() {
        plantImpl.forceExit();
    }

    public AsyncRequest<Facility> createFacilityAReq(final String _name)
            throws Exception {
        return plantImpl.createFacilityAReq(_name);
    }

    public String getActivatorClassName(final String _facilityName) {
        return plantImpl.getActivatorClassName(_facilityName);
    }

    public AsyncRequest<Void> activatorPropertyAReq(final String _facilityName, final String _className) {
        return plantImpl.activatorPropertyAReq(_facilityName, _className);
    }

    public boolean isAutoStart(String name) {
        return plantImpl.isAutoStart(name);
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

    public Object getFailed(String name) {
        return plantImpl.getFailed(name);
    }

    public void failFacility(final String _facilityName, final Object reason) throws Exception {
        plantImpl.failFacility(_facilityName, reason);
    }

    public AsyncRequest<Void> clearFailedAReq(final String _facilityName) {
        return plantImpl.failedAReq(_facilityName, null);
    }

    public boolean isStopped(String name) {
        return plantImpl.isStopped(name);
    }

    public void stopFacility(final String _facilityName) throws Exception {
        plantImpl.stopFacility(_facilityName);
    }

    public AsyncRequest<Void> clearStoppedAReq(final String _facilityName) {
        return plantImpl.stoppedAReq(_facilityName, false);
    }

    public AsyncRequest<Void> recoveryPropertyAReq(final String _facilityName, final Recovery _recovery) {
        return plantImpl.recoveryPropertyAReq(_facilityName, _recovery);
    }

    public AsyncRequest<Void> initialLocalMerssageQueueSizePropertyAReq(final String _facilityName, final Integer _value) {
        return plantImpl.initialLocalMerssageQueueSizePropertyAReq(_facilityName, _value);
    }

    public AsyncRequest<Void> initialBufferSizePropertyAReq(final String _facilityName, final Integer _value) {
        return plantImpl.initialBufferSizePropertyAReq(_facilityName, _value);
    }

    public Facility getFacility(String name) {
        return plantImpl.getFacility(name);
    }
}
