package org.agilewiki.jactor.util.firehose;

import java.util.concurrent.Semaphore;

abstract public class Stage extends Semaphore {

    public Stage(final boolean _fairness) {
        super(1, _fairness);
    }

    abstract public Object process(final Object data);
}
