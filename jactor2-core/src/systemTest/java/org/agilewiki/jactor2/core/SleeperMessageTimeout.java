package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class SleeperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor();
            Hanger hanger = new Hanger(reactor);
            hanger.sleeperSReq().call();
            System.out.println("never gets here");
        } finally {
            plant.close();
        }
    }
}
