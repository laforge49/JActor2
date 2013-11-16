package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.util.Closeable;

/**
 * Base class for sync facility requests.
 */
abstract public class SyncFacilityRequest<RESPONSE_TYPE> extends
        SyncRequest<RESPONSE_TYPE> {

    private final Facility facility;

    /**
     * Create a SyncFacilityRequest.
     *
     * @param _facility The facility on which this request operates.
     */
    public SyncFacilityRequest(final Facility _facility) {
        super(_facility.getReactor());
        facility = _facility;
    }

    /**
     * Add an auto closeable, to be closed when the Facility closes.
     *
     * @param _closeable The closeable to be added to the list.
     * @return True if the Closeable was added.
     */
    protected boolean addAutoClosable(final Closeable _closeable)
            throws Exception {
        return local(facility.addClosableSReq(_closeable));
    }

    /**
     * Remove an auto closeable.
     *
     * @param _closeable The closeable to be removed.
     * @return True if the Closeable was removed.
     */
    protected boolean removeAutoClosable(final Closeable _closeable)
            throws Exception {
        return local(facility.removeClosableSReq(_closeable));
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
}
