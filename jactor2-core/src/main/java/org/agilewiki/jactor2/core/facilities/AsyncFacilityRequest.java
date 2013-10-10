package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

import java.util.Set;

/**
 * Base class for sync facility requests.
 */
abstract public class AsyncFacilityRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

    private final Facility facility;

    /**
     * Create a SyncFacilityRequest.
     *
     * @param _facility The facility on which this request operates.
     */
    public AsyncFacilityRequest(final Facility _facility) {
        super(_facility.getReactor());
        facility = _facility;
    }

    /**
     * Add an auto closeable, to be closed when the Facility closes.
     *
     * @param _closeable The autoclosable to be added to the list.
     * @return True if the AutoClosable was added.
     */
    protected boolean addAutoClosable(final AutoCloseable _closeable) throws Exception {
        return local(facility.addAutoClosableSReq(_closeable));
    }

    /**
     * Remove an auto closeable.
     *
     * @param _closeable The autoclosable to be removed.
     * @return True if the AutoClosable was removed.
     */
    protected boolean removeAutoClosable(final AutoCloseable _closeable) throws Exception {
        return local(facility.removeAutoClosableSReq(_closeable));
    }

    /**
     * Returns the value of a property.
     *
     * @param propertyName The property name.
     * @return The property value, or null.
     */
    protected Object getProperty(final String propertyName) {
        return facility.getProperty(propertyName);
    }

    /**
     * Assign a property value.
     * Or removes it if the value is set to null;
     *
     * @param _propertyName  The name of the property.
     * @param _propertyValue The value of the property, or null.
     * @return The prior value of the property, or null.
     */
    protected Object putProperty(final String _propertyName,
                              final Object _propertyValue) throws Exception {
        return local(facility.putPropertySReq(_propertyName, _propertyValue));
    }

    protected void firstSet(final String _propertyName,
                            final Object _propertyValue) throws Exception {
        local(facility.firstSetSReq(_propertyName, _propertyValue));
    }

    /**
     * Returns a set view of the property names.
     *
     * @return A set view of the property names.
     */
    protected Set<String> getPropertyNames() {
        return facility.getPropertyNames();
    }
}
