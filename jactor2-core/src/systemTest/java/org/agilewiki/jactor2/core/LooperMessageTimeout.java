package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class LooperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor(plant);
            Hanger hanger = new Hanger(reactor);
            hanger.looperSReq().signal();
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            System.out.println("closing");
            plant.close();
            System.out.println("closed");
        }
    }
}
