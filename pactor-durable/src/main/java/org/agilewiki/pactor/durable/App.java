package org.agilewiki.pactor.durable;

public interface App extends PASerializable {
    void setDurable(final Durable _durable);
}
