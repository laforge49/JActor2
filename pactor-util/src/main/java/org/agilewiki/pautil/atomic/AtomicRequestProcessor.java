package org.agilewiki.pautil.atomic;

import java.util.Queue;

import org.agilewiki.pactor.*;

/**
 * An actor which processes a requests one at a time, waiting for each
 * request to complete before starting the next.
 */
public abstract class AtomicRequestProcessor extends ActorBase implements
        Runnable {
    /**
     * A queue of pending requests.
     */
    private Queue<AtomicEntry> entries;

    /**
     * True while a request is being processed.
     */
    private boolean busy;

    /**
     * Creates a queue of pending requests.
     *
     * @return A queue used to hold pending requests.
     */
    protected abstract Queue<AtomicEntry> createQueue();

    @Override
    public void initialize(final Mailbox _mailbox) throws Exception {
        super.initialize(_mailbox);
        entries = createQueue();
    }

    /**
     * The atomicReq is used to request that another request, typically bound to
     * a different actor, is processed to completion before continuing with the
     * next request.
     *
     * @param _request The request to be processed to completion.
     * @return The atomic request.
     */
    public Request<?> atomicReq(final Request _request) {
        return new RequestBase<Object>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Object> _rp)
                    throws Exception {
                entries.offer(new AtomicEntry(_request, _rp));
            }
        };
    }

    /**
     * Called by the mailbox when there are no messages to be processed.
     */
    @Override
    public void run() {
        if (!busy && !entries.isEmpty()) {
            final AtomicEntry entry = entries.remove();
            final Request request = entry.request;
            final ResponseProcessor<Object> _rp = new ResponseProcessor<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    busy = false;
                    entry.rp.processResponse(response);
                }
            };
            getMailbox().setExceptionHandler(new ExceptionHandler() {
                @Override
                public void processException(Throwable throwable)
                        throws Exception {
                    busy = false;
                    _rp.processResponse(throwable);
                }
            });
            busy = true;
            try {
                request.send(getMailbox(), _rp);
            } catch (Exception ex) {
                try {
                    busy = false;
                    _rp.processResponse(ex);
                } catch (Exception ex2) {
                }
            }
        }
    }
}

/**
 * Holds a request to be processed and the ResponseProcessor that gets the result.
 */
class AtomicEntry {
    /**
     * A request to be processed to completion before the next such request is processed.
     */
    public Request request;

    /**
     * The ResponseProcessor that gets the response from the request, or the exception if one occurs.
     */
    public ResponseProcessor<Object> rp;

    /**
     * Create a pending atomic entry.
     *
     * @param _request A request to be processed to completion before the next such request is processed.
     * @param _rp      The ResponseProcessor that gets the response from the request, or the exception if one occurs.
     */
    public AtomicEntry(final Request _request,
            final ResponseProcessor<Object> _rp) {
        request = _request;
        rp = _rp;
    }
}
