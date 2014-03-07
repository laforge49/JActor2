package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.blades.Blade;

public class All extends AsyncRequest<Void> {
    final AsyncRequest<Void>[] requests;

    public All(final Blade _blade, final AsyncRequest<Void>[] _requests) {
        super(_blade.getReactor());
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
