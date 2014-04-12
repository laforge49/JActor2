package org.agilewiki.jactor2.core.mt.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Plant;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class HungRequestTest extends TestCase {
    public void testa() throws Exception {
        new Plant();
        try {
            final Hanger blade1 = new Hanger(new NonBlockingReactor());
            try {
                blade1.hiAReq().call();
            } catch (ReactorClosedException sce) {
            }
            final Hung blade2 = new Hung(new NonBlockingReactor(), new Hanger(new NonBlockingReactor()));
            try {
                blade2.hoAReq().call();
            } catch (ReactorClosedException sce) {
            }
        } finally {
            Plant.close();
        }
    }
}

class Hanger extends NonBlockingBladeBase {

    public Hanger(final NonBlockingReactor mbox) throws Exception {
        super(mbox);
    }

    public AsyncRequest<String> hiAReq() {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                System.out.println("    hang");
            }
        };
    }
}

class Hung extends NonBlockingBladeBase {

    private Hanger hanger;

    public Hung(final NonBlockingReactor mbox, Hanger _hanger) throws Exception {
        super(mbox);
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
