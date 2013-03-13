package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;

import java.util.concurrent.Semaphore;

public final class Pender implements MessageSource {
    public ExceptionHandler exceptionHandler;
    private Semaphore done;
    private transient Object result;

    public Object pend() throws InterruptedException {
        done.acquire();
        return result;
    }

    public void processResponseMessage(ResponseMessage responseMessage) {
        this.result = result;  //todo
        done.release();
    }
}
