package org.agilewiki.jactor.util.firehose;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

abstract public class StageBase extends Semaphore implements Stage {
    private AtomicReference<Engine> reservation = new AtomicReference<Engine>();

    public StageBase() {
        super(1);
    }

    public void makeReservation(final Engine _engine) {
        while (!reservation.compareAndSet(null, _engine)) {
            Thread.yield();
        }
    }

    public void clearReservation() {
        reservation.set(null);
    }
}
