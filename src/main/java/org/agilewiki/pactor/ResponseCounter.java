package org.agilewiki.pactor;

public class ResponseCounter<RESPONSE_TYPE> extends ResponseProcessor<RESPONSE_TYPE> {
    private ResponseProcessor<RESPONSE_TYPE> rp;
    private int count = 0;
    private boolean active = true;
    private RESPONSE_TYPE result;

    public ResponseCounter(ResponseProcessor<RESPONSE_TYPE> rp, RESPONSE_TYPE result) {
        this.rp = rp;
    }

    public void incrementCount() {
        if (active)
            count += 1;
    }

    public void decrementCount() throws Throwable {
        if (active) {
            count -= 1;
            if (count == 0) {
                active = false;
                rp.processResponse(result);
            }
        }
    }

    public void setResult(RESPONSE_TYPE result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void processResponse(RESPONSE_TYPE response) throws Throwable {
        decrementCount();
    }
}
