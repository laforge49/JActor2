package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.plant.DelayAOp;

public class ICloseTest extends CallTestBase {
    public void testa() throws Exception {
        new Plant();
        try {
            call(new IHang().goAOp());
        //} catch (ReactorClosedException e) {
        } finally {
            Plant.close();
        }
    }
}

class IHang extends BlockingBladeBase {

    IHang() throws Exception {
    }

    AOp<Void> goAOp() {
        return new AOp<Void>("go", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                IHung iHung = new IHung();
                final AOp<Void> noRspAOp = iHung.noRspAOp();
                final AsyncRequestImpl<Void> noRspImpl = _asyncRequestImpl.send(noRspAOp, _asyncResponseProcessor);
                _asyncRequestImpl.send(iHung.getReactor().nullSOp(), _asyncResponseProcessor);
                _asyncRequestImpl.send(new DelayAOp(100), new AsyncResponseProcessor<Void>() {
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
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.setNoHungRequestCheck();
                System.out.println("hi");
            }
        };
    }
}
