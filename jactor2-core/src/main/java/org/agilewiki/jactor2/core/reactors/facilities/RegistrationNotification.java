package org.agilewiki.jactor2.core.reactors.facilities;

import org.agilewiki.jactor2.core.blades.NamedBlade;

/**
 * Notification that a blade has registered/unregistered.
 */
public class RegistrationNotification {
    /**
     * The facility which registered the blade.
     */
    public final Facility facility;

    /**
     * The name of the registered blade.
     */
    public final String name;

    /**
     * The registered blade, or null.
     */
    public final NamedBlade blade;

    /**
     * Create a [un]registration notification.
     *
     * @param _facility    The facility which registered the blade.
     * @param _name        The name of the registered blade.
     * @param _blade       The registered blade, or null.
     */
    public RegistrationNotification(final Facility _facility, final String _name, final NamedBlade _blade) {
        facility = _facility;
        name = _name;
        blade = _blade;
    }

    /**
     * Returns true if this is a registration notification.
     *
     * @return True if this is a registration notification.
     */
    public boolean isRegistration() {
        return blade != null;
    }

    /**
     * Returns true if this is an unregistration notification.
     *
     * @return True if this is an unregistration notification.
     */
    public boolean isUnregistration() {
        return blade == null;
    }
}
