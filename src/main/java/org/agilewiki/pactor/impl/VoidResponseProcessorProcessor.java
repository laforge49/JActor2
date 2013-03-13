package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ResponseProcessorInterface;

public final class VoidResponseProcessorProcessor
        implements ResponseProcessorInterface<Void> {
    public final static VoidResponseProcessorProcessor singleton = new VoidResponseProcessorProcessor();

    private VoidResponseProcessorProcessor() {}

    @Override
    public void processResponse(Void response) {
    }

    @Override
    public boolean responseRequired() {
        return false;
    }
}
