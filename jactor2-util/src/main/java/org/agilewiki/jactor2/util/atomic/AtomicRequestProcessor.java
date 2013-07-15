package org.agilewiki.jactor2.util.atomic;

import org.agilewiki.jactor2.api.*;

import java.util.Queue;

/**
 * An actor which processes a requests one at a time, waiting for each
 * boundRequest to complete before starting the next.
 */
public abstract class AtomicRequestProcessor extends ActorBase implements
        Runnable {
    /**
     * A queue of pending requests.
     */
    private Queue<AtomicEntry> entries;

    /**
     * True while a boundRequest is being processed.
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
     * The atomicReq is used to boundRequest that another boundRequest, typically bound to
     * a different actor, is processed to completion before continuing with the
     * next boundRequest.
     *
     * @param _Bound_request The boundRequest to be processed to completion.
     * @return The atomic boundRequest.
     */
    public BoundRequest<?> atomicReq(final BoundRequest _Bound_request) {
        return new BoundRequestBase<Object>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Object> _rp)
                    throws Exception {
                entries.offer(new AtomicEntry(_Bound_request, _rp));
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
            final BoundRequest boundRequest = entry.boundRequest;
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
                boundRequest.send(getMailbox(), _rp);
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
 * Holds a boundRequest to be processed and the ResponseProcessor that gets the result.
 */
class AtomicEntry {
    /**
     * A boundRequest to be processed to completion before the next such boundRequest is processed.
     */
    public BoundRequest boundRequest;

    /**
     * The ResponseProcessor that gets the response from the boundRequest, or the exception if one occurs.
     */
    public ResponseProcessor<Object> rp;

    /**
     * Create a pending atomic entry.
     *
     * @param _Bound_request A boundRequest to be processed to completion before the next such boundRequest is processed.
     * @param _rp            The ResponseProcessor that gets the response from the boundRequest, or the exception if one occurs.
     */
    public AtomicEntry(final BoundRequest _Bound_request,
                       final ResponseProcessor<Object> _rp) {
        boundRequest = _Bound_request;
        rp = _rp;
    }
}
