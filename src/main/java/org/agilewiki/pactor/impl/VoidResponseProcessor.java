package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessor;

public final class VoidResponseProcessor extends ResponseProcessor<Void> {
    public final static VoidResponseProcessor singleton = new VoidResponseProcessor();

    private VoidResponseProcessor() {}

    @Override
    public void processResponse(Void response) {
    }

    @Override
    public boolean responseRequired() {
        return false;
    }
}
