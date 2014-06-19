package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AReq;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Simple {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            A a = new A();
            a.start().call();
        } finally {
            Plant.close();
        }
    }
}

class A extends NonBlockingBladeBase {
    final B b;

    public A() throws Exception {
        b = new B();
    }

    AReq<Void> start() {
        return new AReq<Void>(getReactor()) {
            @Override
            protected void processAsyncRequest(AsyncRequest _asyncRequest,
                                               final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> startResponse = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        System.out.println("added 1");
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };
                _asyncRequest.send(b.add1Areq(), startResponse);
            }
        };
    }
}

class B extends NonBlockingBladeBase {
    private int count;

    public B() throws Exception {
    }

    AReq<Void> add1Areq() {
        return new AReq<Void>(getReactor()) {
            @Override
            protected void processAsyncRequest(AsyncRequest _asyncRequest,
                                               AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                count += 1;
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
