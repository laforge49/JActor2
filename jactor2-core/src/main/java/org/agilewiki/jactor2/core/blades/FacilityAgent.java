package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;

import java.util.Set;

/**
 * A convenience class for implementing facility agents.
 *
 * @param <RESPONSE_TYPE>    The type of response returned by startSReq.
 */
public abstract class FacilityAgent<RESPONSE_TYPE> extends SyncAgentBase<RESPONSE_TYPE, Facility> {
    /**
     * Create a facility agent.
     *
     * @param _facility The facility which is to be local to this agent.
     */
    public FacilityAgent(final Facility _facility) throws Exception {
        super(_facility);
    }

    /**
     * Add an auto closeable, to be closed when the Facility closes.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return True if the AutoClosable was added.
     */
    protected boolean addAutoClosableSReq(final AutoCloseable _closeable) throws Exception {
        return local(localBlade.addAutoClosableSReq(_closeable));
    }

    /**
     * Remove an auto closeable.
     *
     * @param _closeable The autoclosable to be removed.
     * @return True if the AutoClosable was removed.
     */
    protected boolean removeAutoClosableSReq(final AutoCloseable _closeable) throws Exception {
        return local(localBlade.removeAutoClosableSReq(_closeable));
    }

    /**
     * Returns the value of a property.
     *
     * @param propertyName The property name.
     * @return The property value, or null.
     */
    public Object getProperty(final String propertyName) {
        return localBlade.getProperty(propertyName);
    }

    /**
     * Assign a property value.
     * Or removes it if the value is set to null;
     *
     * @param propertyName  The name of the property.
     * @param propertyValue The value of the property, or null.
     * @return The prior value of the property, or null.
     */
    public Object putProperty(final String propertyName,
                              final Object propertyValue) {
        return localBlade.putProperty(propertyName, propertyValue);
    }

    /**
     * Returns a set view of the property names.
     *
     * @return A set view of the property names.
     */
    public Set<String> getPropertyNames() {
        return localBlade.getPropertyNames();
    }
}
