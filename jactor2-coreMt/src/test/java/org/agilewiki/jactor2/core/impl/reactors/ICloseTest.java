package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAOp;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

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

    AOp<Void> goAReq() {
        return new AOp<Void>("go", getReactor()) {
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                IHung iHung = new IHung();
                final AOp<Void> noRspAOp = iHung.noRspAOp();
                final AsyncRequestImpl<Void> noRspImpl = _asyncRequestImpl.send(noRspAOp, _asyncResponseProcessor);
                _asyncRequestImpl.send(iHung.getReactor().nullSOp(), _asyncResponseProcessor);
                _asyncRequestImpl.send(new DelayAOp(50), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response) {
                        _asyncRequestImpl.cancel(noRspImpl);
                    }
                });
            }
        };
    }
}

class IHung extends IsolationBladeBase {

    IHung() throws Exception {
    }

    AOp<Void> noRspAOp() {
        return new AOp<Void>("noRsp", getReactor()) {
            @Override
            public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.setNoHungRequestCheck();
                System.out.println("hi");
            }
        };
    }
}
