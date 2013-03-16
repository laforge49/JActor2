package org.agilewiki.pactor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks request processing, not threads.
 */
public class Semaphore {
    private Mailbox mailbox;
    private int permits;
    private Queue<ResponseProcessor<Void>> queue = new ArrayDeque<ResponseProcessor<Void>>();

    public Semaphore(Mailbox mailbox, int permits) {
        this.mailbox = mailbox;
        this.permits = permits;
    }

    public Request<Void> acquire() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Throwable {
                if (permits > 0) {
                    permits -= 1;
                    responseProcessor.processResponse(null);
                } else {
                    queue.offer(responseProcessor);
                }
            }
        };
    }

    public Request<Void> release() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Void> responseProcessor) throws Throwable {
                final ResponseProcessor<Void> rp = queue.poll();
                if (rp == null) {
                    permits += 1;
                } else {
                    /*
                    mailbox.setExceptionHandler(new ExceptionHandler() {
                        @Override
                        public void processException(Throwable throwable) throws Throwable {
                            ((ResponseProcessor) rp).processResponse(throwable);
                            responseProcessor.processResponse(null);
                        }
                    });
                    */
                    rp.processResponse(null);
                }
                responseProcessor.processResponse(null);
            }
        };
    }
}
