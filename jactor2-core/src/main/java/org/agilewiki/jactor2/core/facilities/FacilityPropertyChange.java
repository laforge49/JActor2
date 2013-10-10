package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.Event;

public class FacilityPropertyChange extends Event<FacilityPropertyChangeSubscriber> {
    private final Facility facility;
    private final String name;
    private final Object oldValue;
    private final Object newValue;

    public FacilityPropertyChange(
            final Facility _facility,
            final String _name,
            final Object _oldValue,
            final Object _newValue
    ) {
        facility = _facility;
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }

    @Override
    protected void processEvent(final FacilityPropertyChangeSubscriber _subscriber) throws Exception {
        _subscriber.propertyChange(facility, name, oldValue, newValue);
    }
}
