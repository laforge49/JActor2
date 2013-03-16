package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessorInterface;

final class VoidResponseProcessor implements ResponseProcessorInterface<Void> {
    public static final VoidResponseProcessor SINGLETON = new VoidResponseProcessor();

    private VoidResponseProcessor() {
    }

    @Override
    public void processResponse(final Void response) {
    }

    @Override
    public boolean responseRequired() {
        return false;
    }
}
