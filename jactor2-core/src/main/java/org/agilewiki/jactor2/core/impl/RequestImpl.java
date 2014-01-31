package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.Request;

public interface RequestImpl<RESPONSE_TYPE> extends AutoCloseable {

    Request asRequest();

    /**
     * Passes this Request together with the AsyncResponseProcessor to the target Reactor.
     * Responses are passed back via the targetReactor of the source blades and processed by the
     * provided AsyncResponseProcessor and any exceptions
     * raised while processing the request are processed by the exception handler active when
     * the doSend method was called.
     *
     * @param _source            The sourceReactor on whose thread this method was invoked and which
     *                           will buffer this Request and subsequently receive the result for
     *                           processing on the same thread.
     * @param _responseProcessor Passed with this request and then returned with the result, the
     *                           AsyncResponseProcessor is used to process the result on the same thread
     *                           that originally invoked this method. If null, then no response is returned.
     */
    void doSend(final ReactorImpl _source,
                          final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception;

    /**
     * Returns true when a response is expected but has not yet been placed in the message.
     *
     * @return True when a response is expected but has not yet been placed in the message.
     */
    boolean isClosed();

    /**
     * Returns true when the request is, directly or indirectly, from an IsolationReactor that awaits a response.
     *
     * @return True whhe request is, directly or indirectly, from an IsolationReactor that awaits a response.
     */
    boolean isIsolated();

    /**
     * Execute the Event.processEvent or AsyncRequest.processAsyncRequest method
     * of the event/request held by the message. This method is always called on the
     * target targetReactor's own thread.
     */
    void eval();

    /**
     * Process the throwable on the current thread in the facility of the active targetReactor.
     *
     * @param _activeReactor The targetReactor providing the facility for processing the throwable.
     * @param _e             The exception to be processed.
     */
    void processException(final ReactorImpl _activeReactor, final Exception _e);

    void close();

    void cancel();

    boolean isCanceled();

    /**
     * Returns true when the target reactor is not also the message source.
     *
     * @return True when the target reactor is not also the message source.
     */
    boolean isForeign();

    boolean isSignal();

    ReactorImpl getTargetReactorImpl();

    RequestSource getRequestSource();

    void responseReceived(RequestImpl request);

    void responseProcessed();
}
