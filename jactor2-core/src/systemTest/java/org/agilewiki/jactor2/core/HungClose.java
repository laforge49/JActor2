package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class HungClose {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.looperSReq().signal();
        } finally {
            System.out.println("closing");
            plant.close();
            System.out.println("closed");
        }
    }
}
