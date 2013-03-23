package org.agilewiki.pactor.util;

import org.agilewiki.pactor.ResponseProcessor;

public class ResponseCounter<RESPONSE_TYPE> implements
        ResponseProcessor<RESPONSE_TYPE> {
    private final ResponseProcessor<RESPONSE_TYPE> rp;
    private int count;
    private boolean active = true;
    private RESPONSE_TYPE result;

    public ResponseCounter(final int _count,
            final ResponseProcessor<RESPONSE_TYPE> _rp,
            final RESPONSE_TYPE response) {
        this.count = _count;
        this.rp = _rp;
    }

    public void incrementCount() {
        if (active)
            count += 1;
    }

    public void decrementCount() throws Exception {
        if (active) {
            count -= 1;
            if (count == 0) {
                active = false;
                rp.processResponse(result);
            }
        }
    }

    public void setResult(final RESPONSE_TYPE _result) {
        this.result = _result;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void processResponse(final RESPONSE_TYPE response) throws Exception {
        decrementCount();
    }
}
