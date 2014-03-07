package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.blades.Blade;

public class Any<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {
    final AsyncRequest<RESPONSE_TYPE>[] requests;

    public Any(final Blade _blade, final AsyncRequest<RESPONSE_TYPE>[] _requests) {
        super(_blade.getReactor());
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
