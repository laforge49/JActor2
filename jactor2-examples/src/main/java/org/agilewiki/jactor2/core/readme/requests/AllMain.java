package org.agilewiki.jactor2.core.readme.requests;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class AllMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            new All(new A1("1"), new A1("2"), new A1("3")).call();
        } finally {
            Plant.close();
        }
    }
}

class All extends AOp<Void> {
    final AOp<Void>[] requests;

    All(final AOp<Void> ... _requests) throws Exception {
        super("All", new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                      final AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {

        AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                if (_asyncRequestImpl.getPendingResponseCount() == 0)
                    _asyncResponseProcessor.processAsyncResponse(null);
            }
        };

        int i = 0;
        while (i < requests.length) {
            _asyncRequestImpl.send(requests[i], responseProcessor);
            i += 1;
        }
    }
}

class A1 extends AOp<Void> {
    A1(String _name) throws Exception {
        super(_name, new NonBlockingReactor());
    }

    @Override
    public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                      AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {
        System.out.println(opName);
        _asyncResponseProcessor.processAsyncResponse(null);
    }
}
