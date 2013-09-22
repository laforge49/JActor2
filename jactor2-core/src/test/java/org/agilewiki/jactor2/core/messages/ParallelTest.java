package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private static final int LOADS = 10;
    private static final long DELAY = 200;

    private Reactor reactor;
    private Facility facility;
    private AsyncRequest<Void> start;

    public void test() throws Exception {
        facility = new Facility();
        reactor = new NonBlockingReactor(facility);

        start = new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;

            @Override
            protected void processAsyncRequest()
                    throws Exception {
                final ResponseCounter<Void> responseCounter = new ResponseCounter<Void>(
                        LOADS, null, dis);
                int i = 0;
                while (i < LOADS) {
                    final Delay dly = new Delay(facility);
                    send(dly.sleepSReq(ParallelTest.DELAY),
                            responseCounter);
                    i += 1;
                }
            }
        };

        final long t0 = System.currentTimeMillis();
        start.call();
        final long t1 = System.currentTimeMillis();
        assertTrue((t1 - t0) < DELAY + DELAY / 2);
        facility.close();
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(ParallelTest.this.reactor);
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _request        The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(final RequestBase<RESPONSE_TYPE> _request,
                                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(reactor, _request, _responseProcessor);
    }
}
