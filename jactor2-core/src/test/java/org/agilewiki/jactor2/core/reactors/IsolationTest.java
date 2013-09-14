package org.agilewiki.jactor2.core.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ResponseCounter;

public class IsolationTest extends TestCase {
    int count = 0;

    public void test() throws Exception {
        Facility facility = new Facility();
        try {
            int _count = startReq1(new IsolationReactor(facility)).call();
            assertEquals(5, _count);
        } finally {
            facility.close();
        }
    }

    AsyncRequest<Integer> startReq1(final Reactor _reactor) {
        return new AsyncRequest<Integer>(_reactor) {
            AsyncRequest<Integer> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                Reactor reactor = new IsolationReactor(_reactor.getFacility());
                AsyncResponseProcessor rc = new ResponseCounter(5, null,
                        new AsyncResponseProcessor() {
                            @Override
                            public void processAsyncResponse(Object response)
                                    throws Exception {
                                dis.processAsyncResponse(count);
                            }
                        });
                aAReq(reactor, 1).send(_reactor, rc);
                aAReq(reactor, 2).send(_reactor, rc);
                aAReq(reactor, 3).send(_reactor, rc);
                aAReq(reactor, 4).send(_reactor, rc);
                aAReq(reactor, 5).send(_reactor, rc);
            }
        };
    }

    AsyncRequest<Void> aAReq(final Reactor _reactor, final int msg) {
        return new AsyncRequest<Void>(_reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest()
                    throws Exception {
                Delay delay = new Delay(_reactor.getFacility());
                delay.sleepSReq(100 - (msg * 20)).send(_reactor,
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(Void response)
                                    throws Exception {
                                if (count != msg - 1)
                                    throw new IllegalStateException();
                                count = msg;
                                dis.processAsyncResponse(null);
                            }
                        });
            }
        };
    }
}
