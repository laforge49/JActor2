package org.agilewiki.jactor2.core.readme.requests;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

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

    All(final AOp<Void>... _requests) throws Exception {
        super("All", new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {

        final AsyncResponseProcessor<Void> responseProcessor = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(final Void _response)
                    throws Exception {
                if (_asyncRequestImpl.hasNoPendingResponses())
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
    A1(final String _name) throws Exception {
        super(_name, new NonBlockingReactor());
    }

    @Override
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {
        System.out.println(getOpName());
        _asyncResponseProcessor.processAsyncResponse(null);
    }
}
