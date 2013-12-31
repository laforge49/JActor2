package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;

public class IncDesTest extends TestCase {
    public void test1() throws Exception {
        System.err.println("\nTest 1");
        final BasicPlant plant = Durables.createPlant();
        try {
            final IncDes a = (IncDes) Durables.newSerializable(plant,
                    IncDes.FACTORY_NAME);
            final int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            plant.close();
        }
    }

    public void test4() throws Exception {
        System.err.println("\nTest 4");
        final BasicPlant plant = Durables.createPlant();
        try {
            final IncDes a = (IncDes) Durables.newSerializable(plant,
                    IncDes.FACTORY_NAME);
            final byte[] bytes = a.getSerializedBytesReq().call();
            final int l = bytes.length;
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            plant.close();
        }
    }

    public void test5() throws Exception {
        System.err.println("\nTest 5");
        final BasicPlant plant = Durables.createPlant();
        try {
            final IncDes a = (IncDes) Durables.newSerializable(plant,
                    IncDes.FACTORY_NAME);
            a.load(new byte[0]);
            final int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            plant.close();
        }
    }

    public void test6() throws Exception {
        System.err.println("\nTest 6");
        final BasicPlant plant = Durables.createPlant();
        try {
            final IncDes jid1 = (IncDes) Durables.newSerializable(plant,
                    IncDes.FACTORY_NAME);
            jid1.load(new byte[0]);
            final Reactor reactor = new NonBlockingReactor();
            final IncDes jid2 = (IncDes) jid1.copyReq(reactor).call();
            final int l = jid2.getDurable().getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
            final boolean eq = jid1.isEqualReq(jid2).call();
            assertTrue(eq);
        } finally {
            plant.close();
        }
    }
}
