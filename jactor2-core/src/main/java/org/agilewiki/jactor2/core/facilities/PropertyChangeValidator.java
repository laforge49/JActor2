package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

public interface PropertyChangeValidator {
    AsyncRequest<String> validatePropertyChangeAReq(String _propertyName, Object oldValue, Object newValue);
}
