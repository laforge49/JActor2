package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class IsolationTest extends CallTestBase {
    public void test1() throws Exception {
        System.out.println("\ntest 1");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            call(foot.dAOp());
        } finally {
            Plant.close();
        }
    }
    
    public void test2() throws Exception {
        System.out.println("\ntest 2");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Via via = new Via(foot.dAOp());
            call(via.dAOp());
        } finally {
            Plant.close();
        }
    }
}

interface IsIt {
    AOp<Void> dAOp();
}

class Head extends IsolationBladeBase implements IsIt {
    private final AOp<Void> d;

    public Head(final AOp<Void> _d) throws Exception {
        d = _d;
    }

    @Override
    public AOp<Void> dAOp() {
        return new AOp<Void>("dHead", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(d, _asyncResponseProcessor);
            }
        };
    }
}

class Foot extends IsolationBladeBase implements IsIt {

    public Foot(final IsolationReactor _reactor) throws Exception {
        super(_reactor);
    }

    @Override
    public AOp<Void> dAOp() {
        return new AOp<Void>("dFoot", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                System.out.println("stomp!");
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}

class Via extends NonBlockingBladeBase implements IsIt {
    private final AOp<Void> d;

    public Via(final AOp<Void> _d) throws Exception {
        d = _d;
    }

    @Override
    public AOp<Void> dAOp() {
        return new AOp<Void>("dVia", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(d, _asyncResponseProcessor);
            }
        };
    }
}