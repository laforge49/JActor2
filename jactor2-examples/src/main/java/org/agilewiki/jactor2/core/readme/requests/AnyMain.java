package org.agilewiki.jactor2.core.readme.requests;

import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class AnyMain {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\ntest 1");
            long x = new Any<Long>(new A2("1", 1), new A2("2", 2), new A2("3",
                    3)).call();
            System.out.println("got " + x);

            System.out.println("\ntest 2");
            x = new Any<Long>(new A3("1", 1), new A3("2", 2), new A3("3", 0))
                    .call();
            System.out.println("got " + x);

            System.out.println("\ntest 3");
            try {
                new Any<Long>(new A3("1", 0), new A3("2", 0), new A3("3", 0))
                        .call();
            } catch (final ForcedException fe) {
                System.out.println("Forced Exception");
            }
        } finally {
            Plant.close();
        }
    }
}

class Any<RESPONSE_TYPE> extends AOp<RESPONSE_TYPE> {
    final AOp<RESPONSE_TYPE>[] requests;

    public Any(final AOp<RESPONSE_TYPE>... _requests) throws Exception {
        super("any", new NonBlockingReactor());
        requests = _requests;
    }

    @Override
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        _asyncRequestImpl
                .setExceptionHandler(new ExceptionHandler<RESPONSE_TYPE>() {
                    @Override
                    public void processException(
                            final Exception e,
                            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
                            throws Exception {
                        if (_asyncRequestImpl.hasNoPendingResponses())
                            throw e;
                    }
                });

        int i = 0;
        while (i < requests.length) {
            _asyncRequestImpl.send(requests[i], _asyncResponseProcessor); //Send the requests and pass back the first result received
            i += 1;
        }
    }
}

class A2 extends AOp<Long> {
    final long delay;

    A2(final String _name, final long _delay) throws Exception {
        super(_name, new NonBlockingReactor());
        delay = _delay;
    }

    @Override
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<Long> _asyncResponseProcessor)
            throws Exception {
        for (long i = 0; i < delay * 100000; i++)
            Thread.yield();
        _asyncResponseProcessor.processAsyncResponse(delay);
    }
}

class ForcedException extends Exception {
}

class A3 extends AOp<Long> {
    final long delay;

    A3(final String _name, final long _delay) throws Exception {
        super(_name, new BlockingReactor());
        delay = _delay;
    }

    @Override
    protected void processAsyncOperation(
            final AsyncRequestImpl _asyncRequestImpl,
            final AsyncResponseProcessor<Long> _asyncResponseProcessor)
            throws Exception {
        if (delay == 0)
            throw new ForcedException();
        for (long i = 0; i < delay * 10000000; i++) {
            if (i % 1000 == 0 && _asyncRequestImpl.isCanceled())
                return;
            Thread.yield();
        }
        _asyncResponseProcessor.processAsyncResponse(delay);
    }
}
