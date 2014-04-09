package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class AllMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            new All(new A1(), new A1(), new A1()).call();
        } finally {
            Plant.close();
        }
    }
}

class All extends AsyncRequest<Void> {
    final AsyncRequest<Void>[] requests;

    All(final AsyncRequest<Void> ... _requests) {
        super(new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    public void processAsyncRequest() throws Exception {

        AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                if (getPendingResponseCount() == 0)
                    All.this.processAsyncResponse(null);
            }
        };

        int i = 0;
        while (i < requests.length) {
            send(requests[i], responseProcessor);
            i += 1;
        }
    }
}

class A1 extends AsyncRequest<Void> {
    A1() {
        super(new NonBlockingReactor());
    }

    @Override
    public void processAsyncRequest() {
        System.out.println("A1");
        processAsyncResponse(null);
    }
}
