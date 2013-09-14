package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;

public class IncDesTest extends TestCase {
    public void test1() throws Exception {
        System.err.println("\nTest 1");
        Facility facility = Durables.createFacility();
        try {
            IncDes a = (IncDes) Durables.newSerializable(facility, IncDes.FACTORY_NAME);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            facility.close();
        }
    }

    public void test4() throws Exception {
        System.err.println("\nTest 4");
        Facility facility = Durables.createFacility();
        try {
            IncDes a = (IncDes) Durables.newSerializable(facility, IncDes.FACTORY_NAME);
            byte[] bytes = a.getSerializedBytesReq().call();
            int l = bytes.length;
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            facility.close();
        }
    }

    public void test5() throws Exception {
        System.err.println("\nTest 5");
        Facility facility = Durables.createFacility();
        try {
            IncDes a = (IncDes) Durables.newSerializable(facility, IncDes.FACTORY_NAME);
            a.load(new byte[0]);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            facility.close();
        }
    }

    public void test6() throws Exception {
        System.err.println("\nTest 6");
        Facility facility = Durables.createFacility();
        try {
            IncDes jid1 = (IncDes) Durables.newSerializable(facility, IncDes.FACTORY_NAME);
            jid1.load(new byte[0]);
            Reactor reactor = new NonBlockingReactor(facility);
            IncDes jid2 = (IncDes) jid1.copyReq(reactor).call();
            int l = jid2.getDurable().getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
            boolean eq = jid1.isEqualReq(jid2).call();
            assertTrue(eq);
        } finally {
            facility.close();
        }
    }
}
