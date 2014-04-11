package org.agilewiki.jactor2.core.mt;

public class SleeperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.sleeperSReq().call();
            System.out.println("never gets here");
        } finally {
            Plant.close();
        }
    }
}