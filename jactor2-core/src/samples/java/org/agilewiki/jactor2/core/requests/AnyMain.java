package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class AnyMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            new Any(new A2(1), new A2(2), new A2(3)).call();
        } finally {
            Plant.close();
        }
    }
}

class Any<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {
    final AsyncRequest<RESPONSE_TYPE>[] requests;

    public Any(final AsyncRequest<RESPONSE_TYPE> ... _requests) {
        super(new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    public void processAsyncRequest() throws Exception {

        setExceptionHandler(new ExceptionHandler<RESPONSE_TYPE>() {
            @Override
            public void processException(Exception e, AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor) throws Exception {
                if (getPendingResponseCount() == 0)
                    throw e;
            }
        });

        int i = 0;
        while (i < requests.length) {
            send(requests[i], this);
            i += 1;
        }
    }
}

class A2 extends AsyncRequest<Void> {
    volatile long delay;

    A2(final long _delay) {
        super(new BlockingReactor());
        delay = _delay;
    }

    @Override
    public void onCancel() {
        delay = 0;
    }

    @Override
    public void processAsyncRequest() throws Exception {
        for (long i = 0; i < delay * 100000000; i++);
        if (delay > 0)
            System.out.println("A2 "+delay);
        processAsyncResponse(null);
    }
}
