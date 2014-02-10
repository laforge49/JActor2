package org.agilewiki.jactor2.modules;

public class FacilityAlreadyPresentException extends Exception {
    public FacilityAlreadyPresentException(final String _name) {
        super(_name);
    }
}
