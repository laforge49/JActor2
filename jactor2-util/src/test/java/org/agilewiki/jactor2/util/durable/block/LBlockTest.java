package org.agilewiki.jactor2.util.durable.block;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.incDes.Root;

public class LBlockTest extends TestCase {
    public void test() throws Exception {
        final BasicPlant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            final Root rj = (Root) Durables.newSerializable(factoryLocator,
                    Root.FACTORY_NAME, new NonBlockingReactor());
            final LBlock lb1 = new LBlock();
            lb1.setRootJid(rj);
            final byte[] bs = lb1.serialize();

            final int hl = lb1.headerLength();
            final int rjl = rj.getSerializedLength();
            assertEquals(bs.length, hl + rjl);

            final byte[] h = new byte[hl];
            System.arraycopy(bs, 0, h, 0, hl);
            final byte[] sd = new byte[rjl];
            System.arraycopy(bs, hl, sd, 0, rjl);

            final LBlock lb2 = new LBlock();
            final int rjl2 = lb2.setHeaderBytes(h);
            lb2.setRootBytes(sd);
            final Root rj2 = lb2.getRoot(factoryLocator,
                    new NonBlockingReactor(), null);
        } finally {
            plant.close();
        }
    }
}
