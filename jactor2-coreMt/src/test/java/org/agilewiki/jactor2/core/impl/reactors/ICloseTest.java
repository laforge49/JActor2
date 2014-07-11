package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAOp;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class ICloseTest extends CallTestBase {
    public void testa() throws Exception {
        new Plant();
        try {
            call(new IHang().goAReq());
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
                send(iHung.getReactor().nullSOp(), dis);
                send(new DelayAOp(50), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) {
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
            public void processAsyncRequest() throws Exception {
                setNoHungRequestCheck();
                System.out.println("hi");
            }
        };
    }
}
