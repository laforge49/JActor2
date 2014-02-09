package org.agilewiki.jactor2.core.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.plant.DelayAReq;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.BoundResponseProcessor;

public class ICloseTest extends TestCase {
    public void testa() throws Exception {
        new Plant();
        try {
            new IHang().goAReq().call();
        } finally {
            Plant.close();
        }
    }
}

class IHang extends BlockingBladeBase {

    IHang() throws Exception {
    }

    AsyncRequest<Void> goAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;
            IHung iHung;

            @Override
            public void processAsyncRequest() throws Exception {
                iHung = new IHung();
                final AsyncRequest<Void> noRspAReq = iHung.noRspAReq();
                send(noRspAReq, dis);
                send(iHung.getReactor().nullSReq(), dis);
                send(new DelayAReq(50), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        cancel(noRspAReq);
                    }
                });
            }
        };
    }
}

class IHung extends IsolationBladeBase {

    IHung() throws Exception {
    }

    AsyncRequest<Void> noRspAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void onCancel() {
                System.out.println("blip");
                try {
                    new BoundResponseProcessor<Void>(IHung.this, this).processAsyncResponse(null);
                } catch (final Exception e) {}
            }

            @Override
            public void processAsyncRequest() throws Exception {
                setNoHungRequestCheck();
                System.out.println("hi");
            }
        };
    }
}