package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * Properties are validated before being applied.
 */
public interface PropertyChangeValidator {
    /**
     * The request returns a null if the property is valid, otherwise it returns an error message.
     *
     * @param _propertyName The name of the property.
     * @param _oldValue     The old value.
     * @param _newValue     The proposed new value.
     * @return Error message or null.
     */
    AsyncRequest<String> validatePropertyChangeAReq(String _propertyName,
            Object _oldValue, Object _newValue);
}
