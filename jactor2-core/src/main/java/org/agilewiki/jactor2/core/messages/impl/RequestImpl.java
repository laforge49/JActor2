package org.agilewiki.jactor2.core.messages.impl;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.Operation;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.alt.SyncNativeRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * API for internal request implementations.
 *
 * @param <RESPONSE_TYPE>    The return value type.
 */
public interface RequestImpl<RESPONSE_TYPE> extends AutoCloseable, Comparable<RequestImpl> {

    /**
     * Returns the Request implemented by this RequestImpl.
     *
     * @return The Request implemented by this RequestImpl.
     */
    Operation<RESPONSE_TYPE> asOperation();

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
            final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor);

    /**
     * Returns true if the request has been canceled.
     *
     * @return True if the request has been canceled.
     * @throws ReactorClosedException when the request has been closed.
     */
    boolean isCanceled() throws ReactorClosedException;

    /**
     * Returns the target Reactor.
     * @return The target Reactor.
     */
    Reactor getTargetReactor();

    /**
     * Returns the source reactor, or null.
     * A null is returned if the request was passed using the signal or call methods.
     *
     * @return The source reactor or null.
     */
    Reactor getSourceReactor();

    /**
     * Passes this Request to the target Reactor without any result being passed back.
     * I.E. The signal method results in a 1-way message being passed.
     * If an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     */
    void signal();

    /**
     * Passes this Request to the target Reactor and blocks the current thread until
     * a result is returned. The call method sends the message directly without buffering,
     * as there is no source reactor. The response message is buffered, though thread migration is
     * not possible.
     *
     * @return The response value from applying this Request to the target reactor.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    @GwtIncompatible
    RESPONSE_TYPE call() throws Exception;

    <RT> RT syncDirect(final SOp<RT> _sOp)
            throws Exception;

    <RT> RT syncDirect(final SyncNativeRequest<RT> _syncNativeRequest)
            throws Exception;

    void setMessageTimeoutMillis(int _timeoutMillis);

    int getMessageTimeoutMillis();
}
