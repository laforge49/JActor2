package org.agilewiki.jactor.util.fbp;

public interface OutPort extends Activity, AutoCloseable {

    FActor getTarget();

    boolean full();

    void write(final Object _e);
}
