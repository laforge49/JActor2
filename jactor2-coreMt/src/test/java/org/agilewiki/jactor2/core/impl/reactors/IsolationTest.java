package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.mtRequests.RequestMtImpl;
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
            System.err.println("skipping this test");
            assertFalse(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test3a() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 3a");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Head head = new Head(foot.dAOp());
            head.addResource(foot);
            assertTrue(call(head.dAOp()));
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
            System.err.println("skipping this test");
            assertFalse(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test4a() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 4a");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            Via via = new Via(foot.dAOp());
            Head head = new Head(via.dAOp());
            head.addResource(foot);
            assertTrue(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test4b() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 4b");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            IVia via = new IVia(foot.dAOp());
            Head head = new Head(via.dAOp());
            head.addResource(via);
            via.addResource(foot);
            assertTrue(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test4c() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 4c");
        new Plant();
        try {
            Foot foot = new Foot(new IsolationReactor());
            IVia via = new IVia(foot.dAOp());
            Head head = new Head(via.dAOp());
            head.addResource(via);
            head.addResource(foot);
            System.err.println("skipping this test");
            assertFalse(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test5() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 5");
        new Plant();
        try {
            IsolationReactor reactor = new IsolationReactor();
            Foot foot = new Foot(reactor);
            Via via = new Via(foot.dAOp());
            Head head = new Head(via.dAOp(), reactor);
            assertTrue(call(head.dAOp()));
        } finally {
            Plant.close();
        }
    }

    public void test6() throws Exception {
        Thread.sleep(100);
        System.err.println("\ntest 6");
        new Plant();
        try {
            IsolationReactor reactor = new IsolationReactor();
            Foot foot = new Foot(reactor);
            Head head = new Head(foot.dAOp(), reactor);
            assertTrue(call(head.dAOp()));
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

    public Head(final AOp<Boolean> _d, final IsolationReactor _reactor) throws Exception {
        super(_reactor);
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
                        System.err.println("woops!!! "+e.getClass().getName());
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
                System.err.println("dFoot getIsolated: "+((RequestMtImpl)_asyncRequestImpl).getIsolationReactor());
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
                System.err.println("dVia getIsolated: "+((RequestMtImpl)_asyncRequestImpl).getIsolationReactor());
                _asyncRequestImpl.send(d, _asyncResponseProcessor);
            }
        };
    }
}

class IVia extends IsolationBladeBase implements IsIt {
    private final AOp<Boolean> d;

    public IVia(final AOp<Boolean> _d) throws Exception {
        d = _d;
    }

    @Override
    public AOp<Boolean> dAOp() {
        return new AOp<Boolean>("dVia", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Boolean> _asyncResponseProcessor)
                    throws Exception {
                System.err.println("dIVia getIsolated: "+((RequestMtImpl)_asyncRequestImpl).getIsolationReactor());
                _asyncRequestImpl.send(d, _asyncResponseProcessor);
            }
        };
    }
}