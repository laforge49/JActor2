package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class LooperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor();
            Hanger hanger = new Hanger(reactor);
            hanger.looperSReq().signal();
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            plant.close();
        }
    }
}
