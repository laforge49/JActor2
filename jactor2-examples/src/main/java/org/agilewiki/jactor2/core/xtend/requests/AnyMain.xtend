package org.agilewiki.jactor2.core.xtend.requests;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

class AnyMain {
    def static void main(String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\ntest 1");
            var x = new Any<Long>(new A2(1), new A2(2), new A2(3)).call();
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
    val AsyncRequest<RESPONSE_TYPE>[] requests;

    new(AsyncRequest<RESPONSE_TYPE>... _requests) throws Exception {
        super(new NonBlockingReactor());
        requests = _requests;
    }

    override void processAsyncRequest() throws Exception {

        setExceptionHandler(new ExceptionHandler<RESPONSE_TYPE>() {
            override void processException(
                    Exception e,
                    AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
                    throws Exception {
                if (getPendingResponseCount() == 0)
                    throw e;
            }
        });

		for (r : requests) {
            send(r, this); //Send the requests and pass back the first result received
		}
    }
}

class A2 extends AsyncRequest<Long> {
    val long delay;

    new(long _delay) throws Exception {
        super(new NonBlockingReactor());
        delay = _delay;
    }

    override void processAsyncRequest() {
        for (var i = 0L; i < delay * 100000; i++)
            Thread.yield();
        processAsyncResponse(delay);
    }
}

class ForcedException extends Exception {
}

class A3 extends AsyncRequest<Long> {
    val long delay;

    new(long _delay) throws Exception {
        super(new BlockingReactor());
        delay = _delay;
    }

    override void processAsyncRequest() throws ForcedException {
        if (delay == 0)
            throw new ForcedException();
        for (var i = 0L; i < delay * 10000000; i++) {
            if (i % 1000 == 0 && isCanceled())
                return;
            Thread.yield();
        }
        processAsyncResponse(delay);
    }
}
