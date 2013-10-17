package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.SyncRequest;

public interface PropertyChangeValidator {
    SyncRequest<String> validatePropertyChange(String _propertyName, Object oldValue, Object newValue);
}
