package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Blade;

public interface FacilityPropertyChangeSubscriber extends Blade {
    void propertyChange(final Facility _facility, final String _name, final Object _oldValue, final Object _newValue);
}
