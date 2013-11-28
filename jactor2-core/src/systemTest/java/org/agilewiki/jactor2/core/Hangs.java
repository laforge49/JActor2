package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Hangs {
    static public void main(final String[] _args) throws Exception {
        System.out.println("?");
        final Plant plant = new Plant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor(plant);
            Hanger hanger = new Hanger(reactor);
            hanger.hangSReq().signal();
        } finally {
            System.out.println("closing");
            plant.close();
            System.out.println("closed");
        }
    }
}

class Hanger extends BladeBase {
    Hanger(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    SyncRequest<Void> hangSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                while (true) {}
            }
        };
    }
}
