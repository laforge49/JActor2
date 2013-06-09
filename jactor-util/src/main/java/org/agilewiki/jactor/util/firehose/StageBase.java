package org.agilewiki.jactor.util.firehose;

import java.util.concurrent.Semaphore;

abstract public class StageBase extends Semaphore implements Stage {

    public StageBase(final boolean _fairness) {
        super(1, _fairness);
    }

    abstract public Object process(final Object data);
}
