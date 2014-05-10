package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.JActorStTestPlantConfiguration;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class GwtTestIsolationTest extends BaseGWTTestCase {
    public void testa() throws Exception {
        final JActorStTestPlantConfiguration config = new JActorStTestPlantConfiguration();
        new Plant(new JActorStTestPlantConfiguration());
        try {
            final Iso1 iso1 = new Iso1();
            iso1.startAReq().signal();
        } finally {
            Plant.close();
        }
    }
}

class Iso1 extends NonBlockingBladeBase {
    Iso1() {
        super(new NonBlockingReactor());
    }

    AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            AsyncResponseProcessor<Void> doResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    if (getPendingResponseCount() == 0)
                        dis.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                /*
                Iso2 iso2 = new Iso2(new NonBlockingReactor());
                send(iso2.fooAReq(), doResponseProcessor);
                send(iso2.fooAReq(), doResponseProcessor);
                send(iso2.fooAReq(), doResponseProcessor);
                */
                final Iso2 iso2 = new Iso2(new IsolationReactor());
                send(iso2.fooAReq(), doResponseProcessor);
                send(iso2.fooAReq(), doResponseProcessor);
                send(iso2.fooAReq(), doResponseProcessor);
            }
        };
    }
}

class Iso2 extends BladeBase {
    Iso2(final Reactor reactor) {
        _initialize(reactor);
    }

    AsyncRequest<Void> fooAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                final Iso3 iso3 = new Iso3();
                System.out.println("begin");
                send(iso3.barAReq(), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        System.out.println("end");
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}

class Iso3 extends NonBlockingBladeBase {
    Iso3() {
        super(new NonBlockingReactor());
    }

    AsyncRequest<Void> barAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                this.processAsyncResponse(null);
            }
        };
    }
}
