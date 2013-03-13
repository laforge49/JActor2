package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessor;

public class VoidResponseProcessor extends ResponseProcessor<Void> {

    @Override
    public void processResponse(Void response) {
    }

    @Override
    public boolean responseRequired() {
        return false;
    }
}
