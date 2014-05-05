package org.agilewiki.jactor2.core.impl.blades.transactions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.blades.transactions.SyncUpdate;
import org.agilewiki.jactor2.core.blades.transactions.Transaction;
import org.agilewiki.jactor2.core.impl.Plant;

import java.io.IOException;

public class IdentityTest extends TestCase {
    public void testI() throws Exception {
        new Plant();

        Transaction<String> addGood = new Transaction(new SyncUpdate<String>() {
            @Override
            public String update(ImmutableReference<String> source, Transaction<String> target) {
                return "good " + source.getImmutable();
            }
        });

        Transaction<String> addMoreGood = new Transaction(addGood, new SyncUpdate<String>() {
            @Override
            public String update(ImmutableReference<String> source, Transaction<String> target) {
                return "more " + source.getImmutable();
            }
        });

        Transaction<String> bogus = new Transaction(addGood, new SyncUpdate<String>() {
            @Override
            public String update(ImmutableReference<String> source, Transaction<String> target) throws Exception {
                throw new NullPointerException();
            }
        });

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
            } catch(Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.println(m.getImmutable()); // times
        } finally {
            Plant.close();
        }
    }
}
