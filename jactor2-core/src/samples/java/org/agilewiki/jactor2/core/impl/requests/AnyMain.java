package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

public class AnyMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\ntest 1");
            long x = new Any<Long>(new A2(1), new A2(2), new A2(3)).call();
            System.out.println("got " + x);

            System.out.println("\ntest 2");
            x = new Any<Long>(new A3(1), new A3(2), new A3(0)).call();
            System.out.println("got " + x);

            System.out.println("\ntest 3");
            try {
                new Any<Long>(new A3(0), new A3(0), new A3(0)).call();
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

    public Any(final AsyncRequest<RESPONSE_TYPE>... _requests) {
        super(new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    public void processAsyncRequest() throws Exception {

        setExceptionHandler(new ExceptionHandler<RESPONSE_TYPE>() {
            @Override
            public void processException(
                    Exception e,
                    AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
                    throws Exception {
                if (getPendingResponseCount() == 0)
                    throw e;
            }
        });

        int i = 0;
        while (i < requests.length) {
            send(requests[i], this); //Send the requests and pass back the first result received
            i += 1;
        }
    }
}

class A2 extends AsyncRequest<Long> {
    final long delay;

    A2(final long _delay) {
        super(new NonBlockingReactor());
        delay = _delay;
    }

    @Override
    public void processAsyncRequest() {
        for (long i = 0; i < delay * 100000; i++)
            Thread.yield();
        processAsyncResponse(delay);
    }
}

class ForcedException extends Exception {
}

class A3 extends AsyncRequest<Long> {
    final long delay;

    A3(final long _delay) {
        super(new BlockingReactor());
        delay = _delay;
    }

    @Override
    public void processAsyncRequest() throws ForcedException {
        if (delay == 0)
            throw new ForcedException();
        for (long i = 0; i < delay * 10000000; i++) {
            if (i % 1000 == 0 && isCanceled())
                return;
            Thread.yield();
        }
        processAsyncResponse(delay);
    }
}
