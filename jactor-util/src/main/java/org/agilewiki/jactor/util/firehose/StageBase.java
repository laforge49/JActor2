package org.agilewiki.jactor.util.firehose;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

abstract public class StageBase extends Semaphore implements Stage {
    private AtomicReference<Engine> reservation = new AtomicReference<Engine>();

    public StageBase() {
        super(1);
    }
}
