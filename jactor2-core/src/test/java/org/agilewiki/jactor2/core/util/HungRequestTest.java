package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class HungRequestTest extends TestCase {
    public void testa() throws Exception {
        final Plant plant = new Plant();
        try {
            final Hanger blade1 = new Hanger(new NonBlockingReactor(plant));
            try {
                blade1.hiAReq().call();
            } catch (ServiceClosedException sce) {
            }
            final Hung blade2 = new Hung(new NonBlockingReactor(plant), new Hanger(new NonBlockingReactor(plant)));
            try {
                blade2.hoAReq().call();
            } catch (ServiceClosedException sce) {
            }
        } finally {
            plant.close();
        }
    }
}

class Hanger extends NonBlockingBladeBase {

    public Hanger(final NonBlockingReactor mbox) throws Exception {
        initialize(mbox);
    }

    public AsyncRequest<String> hiAReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                System.out.println("hang");
            }
        };
    }
}

class Hung extends NonBlockingBladeBase {

    private Hanger hanger;

    public Hung(final NonBlockingReactor mbox, Hanger _hanger) throws Exception {
        initialize(mbox);
        hanger = _hanger;
    }

    public AsyncRequest<String> hoAReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                send(hanger.hiAReq(), this);
            }
        };
    }
}
