package org.agilewiki.jactor2.core.impl.blades.transactions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.blades.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.impl.Plant;

public class SyncTest extends TestCase {
    public void testI() throws Exception {
        new Plant();

        SyncTransaction<String> addGood = new SyncTransaction<String>() {
            @Override
            protected void update(ImmutableReference<String> source) throws Exception {
                immutable = "good " + source.getImmutable();
            }
        };

        SyncTransaction<String> addMoreGood = new SyncTransaction<String>(addGood) {
            @Override
            public void update(ImmutableReference<String> source) {
                immutable = "more " + source.getImmutable();
            }
        };

        SyncTransaction<String> bogus = new SyncTransaction<String>(addGood) {
            @Override
            public void update(ImmutableReference<String> source) throws Exception {
                throw new NullPointerException();
            }
        };

        try {
            ImmutableReference m = new ImmutableReference<String>("fun");
            System.out.println(m.getImmutable()); // fun
            addGood.applyAReq(m).call();
            System.out.println(m.getImmutable()); // good fun
            m = new ImmutableReference<String>("grapes");
            System.out.println(m.getImmutable()); // grapes
            addMoreGood.applyAReq(m).call();
            System.out.println(m.getImmutable()); // more good grapes
            m = new ImmutableReference<String>("times");
            System.out.println(m.getImmutable()); // times
            try {
                bogus.applyAReq(m).call();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.println(m.getImmutable()); // times
        } finally {
            Plant.close();
        }
    }
}
