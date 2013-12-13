package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class HungRequestTest extends TestCase {
    public void testa() throws Exception {
        final Plant plant = new Plant();
        try {
            final Reactor reactor = new NonBlockingReactor(plant);
            final Hanger blade1 = new Hanger(reactor);
            try {
                blade1.hiAReq().call();
            } catch (ServiceClosedException sce) {
            }
        } finally {
            plant.close();
        }
    }
}

class Hanger extends BladeBase {

    public Hanger(final Reactor mbox) throws Exception {
        initialize(mbox);
    }

    public AsyncRequest<String> hiAReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                System.out.println("hang");
            }
        };
    }
}
