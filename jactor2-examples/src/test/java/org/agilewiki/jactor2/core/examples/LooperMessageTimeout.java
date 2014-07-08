package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.impl.Plant;

public class LooperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.looperSOp().signal();
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            Plant.close();
        }
    }
}
