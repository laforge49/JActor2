package org.agilewiki.pactor.util.durable;

public interface App extends PASerializable {
    void setDurable(final Durable _durable);
}
