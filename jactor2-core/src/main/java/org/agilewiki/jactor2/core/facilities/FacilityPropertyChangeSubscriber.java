package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public interface FacilityPropertyChangeSubscriber extends Blade {
    AsyncRequest<Void> propertyChangedAReq(final Facility _facility,
            final String _name, final Object _oldValue, final Object _newValue);
}
