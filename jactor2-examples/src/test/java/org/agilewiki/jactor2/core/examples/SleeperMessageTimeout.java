package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.impl.Plant;

public class SleeperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.sleeperSOp().call();
            System.out.println("never gets here");
        } finally {
            Plant.close();
        }
    }
}
