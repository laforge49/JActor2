package org.agilewiki.pautil;

import org.agilewiki.pactor.api.ResponseProcessor;

/**
 * When multiple requests have been sent, ResponseCounter can be used to
 * track the number of requests that have not yet completed.
 *
 * @param <RESPONSE_TYPE> The type of the response to be given when all outstanding
 *                        requests have been completed.
 */
public class ResponseCounter<RESPONSE_TYPE> implements
        ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The ResponseProcessor to be used when all outstanding requests have completed.
     */
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    /**
     * The number of requests that have not yet completed.
     */
    private int count;

    /**
     * True when there are still incomplete requests.
     */
    private boolean active = true;

    /**
     * The response to be returned when all the requests have completed.
     */
    private RESPONSE_TYPE result;

    /**
     * Create a ResponseCounter.
     *
     * @param _count   The number of outstanding requests.
     * @param response The response to be given when all requests have completed.
     * @param _rp      The ResponseProcessor to be used when all outstanding requests have completed.
     */
    public ResponseCounter(final int _count, final RESPONSE_TYPE response,
            final ResponseProcessor<RESPONSE_TYPE> _rp) {
        this.count = _count;
        this.rp = _rp;
    }

    /**
     * When an additional request has been sent, incrementCount needs to be called.
     */
    public void incrementCount() {
        if (active)
            count += 1;
    }

    /**
     * When an ExceptionHandler has caught an exception from one of the outstanding requests,
     * decrementCount needs to be called.
     */
    public void decrementCount() throws Exception {
        if (active) {
            count -= 1;
            if (count == 0) {
                active = false;
                rp.processResponse(result);
            }
        }
    }

    /**
     * The setResult method can be used to change the result to be given when all outstanding
     * requests have completed.
     *
     * @param _result The new result to be given.
     */
    public void setResult(final RESPONSE_TYPE _result) {
        this.result = _result;
    }

    /**
     * Returns the number of outstanding requests remaining.
     *
     * @return The number of outstanding requests remaining.
     */
    public int getCount() {
        return count;
    }

    /**
     * This method is called as each request completes.
     *
     * @param response The response given, which is ignored.
     */
    @Override
    public void processResponse(final RESPONSE_TYPE response) throws Exception {
        decrementCount();
    }
}
