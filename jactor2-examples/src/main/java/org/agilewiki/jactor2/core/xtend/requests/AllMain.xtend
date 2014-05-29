package org.agilewiki.jactor2.core.xtend.requests;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

class AllMain {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            new All(new A1(), new A1(), new A1()).call();
        } finally {
            Plant.close();
        }
    }
}

class All extends AsyncRequest<Void> {
    val AsyncRequest<Void>[] requests;

    new(AsyncRequest<Void>... _requests) throws Exception {
        super(new NonBlockingReactor());
        requests = _requests;
    }

    override void processAsyncRequest() throws Exception {

        val responseProcessor = new AsyncResponseProcessor<Void>() {
            override void processAsyncResponse(Void _response)
                    throws Exception {
                if (getPendingResponseCount() == 0)
                    All.this.processAsyncResponse(null);
            }
        };

		for (r : requests) {
			send(r, responseProcessor);
		}
    }
}

class A1 extends AsyncRequest<Void> {
    new() throws Exception {
        super(new NonBlockingReactor());
    }

    override void processAsyncRequest() {
        System.out.println("A1");
        processAsyncResponse(null);
    }
}
