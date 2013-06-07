package org.agilewiki.jactor.util.fbp;

public interface InPort extends Activity, AutoCloseable {

    FActor getSource();
}
