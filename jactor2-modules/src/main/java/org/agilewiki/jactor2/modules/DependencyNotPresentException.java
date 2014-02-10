package org.agilewiki.jactor2.modules;

public class DependencyNotPresentException extends Exception {
    public DependencyNotPresentException(final String _name) {
        super(_name);
    }
}
