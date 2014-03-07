package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class AnyMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\ntest 1");
            long x =new Any<Long>(new A2(1), new A2(2), new A2(0)).call();
            System.out.println("got " + x);

            System.out.println("\ntest 2");
            try {
                new Any<Long>(new A2(0), new A2(0), new A2(0)).call();
            } catch (ForcedException fe) {
                System.out.println("Forced Exception");
            }
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

class A2 extends AsyncRequest<Long> {
    volatile long delay;

    A2(final long _delay) {
        super(new BlockingReactor());
        delay = _delay;
    }

    @Override
    public void onCancel() {
        System.out.println("canceled: " + delay);
        delay = 0;
    }

    @Override
    public void processAsyncRequest() throws Exception {
        if (delay == 0)
            throw new ForcedException();
        for (long i = 0; i < delay * 100000000; i++);
        processAsyncResponse(delay);
    }
}

class ForcedException extends Exception {}
