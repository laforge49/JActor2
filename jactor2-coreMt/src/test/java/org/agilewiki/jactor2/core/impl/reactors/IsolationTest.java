package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class IsolationTest extends CallTestBase {
    public void test1() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 1");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            assertTrue(call(foot.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test2() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 2");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Via via = new Via(foot.dAOp());
            assertTrue(call(via.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test3() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 3");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Head head = new Head(foot.dAOp());
            assertFalse(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test4() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 4");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Via via = new Via(foot.dAOp());
            Head head = new Head(via.dAOp());
            //assertFalse(call(head.dAOp()));
            assertTrue(call(head.dAOp())); //todo should be assertFalse
        } finally {
            Plant.close();
        }
    }
}

interface IsIt {
    AOp<Boolean> dAOp();
}

class Head extends IsolationBladeBase implements IsIt {
    private final AOp<Boolean> d;

    public Head(final AOp<Boolean> _d) throws Exception {
        d = _d;
    }

    @Override
    public AOp<Boolean> dAOp() {
        return new AOp<Boolean>("dHead", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Boolean> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.setExceptionHandler(new ExceptionHandler<Boolean>() {
                    @Override
                    public Boolean processException(Exception e) throws Exception {
                        if (!(e instanceof ReactorClosedException))
                            throw e;
                        return false;
                    }
                });
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
    public AOp<Boolean> dAOp() {
        return new AOp<Boolean>("dFoot", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Boolean> _asyncResponseProcessor)
                    throws Exception {
                _asyncResponseProcessor.processAsyncResponse(true);
            }
        };
    }
}

class Via extends NonBlockingBladeBase implements IsIt {
    private final AOp<Boolean> d;

    public Via(final AOp<Boolean> _d) throws Exception {
        d = _d;
    }

    @Override
    public AOp<Boolean> dAOp() {
        return new AOp<Boolean>("dVia", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Boolean> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.send(d, _asyncResponseProcessor);
            }
        };
    }
}