package org.agilewiki.jactor2.core.impl;

public class HungClose {
    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.looperSReq().signal();
        } finally {
            System.out.println("closing");
            Plant.close();
            System.out.println("closed");
        }
    }
}
