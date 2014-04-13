package org.agilewiki.jactor2.core.impl.stPlant;

import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;

/**
 * Base class for managing failure detection and recovery.
 * The default Recovery is created by PlantConfiguration.
 */
public class Recovery {

    /**
     * Handles hung request. Default action: close the reactor.
     *
     * @param _requestImpl    The reactor with the hung request.
     */
    public void onHungRequest(final RequestImpl _requestImpl) throws Exception {
        ReactorImpl reactor = _requestImpl.getTargetReactorImpl();
        reactor.error("request hung -> reactor close");
        reactor.fail("hung request");
    }

    /**
     * Handles StackOverflowError. Default action: close the reactor.
     *
     * @param _requestImpl    The reactor with the hung request.
     * @param _error          The StackOverflowError.
     */
    public void onStackOverflowError(final RequestImpl _requestImpl, final StackOverflowError _error) {
        ReactorImpl reactor = _requestImpl.getTargetReactorImpl();
        reactor.error("stack overflow error -> reactor close", _error);
        try {
            reactor.fail("stack overflow");
        } catch (Exception e) {

        }
    }

    /**
     * Handles RuntimeException. Default action: close the reactor.
     *
     * @param _requestImpl    The reactor with the hung request.
     * @param _exception      The runtime exception
     */
    public void onRuntimeException(final RequestImpl _requestImpl, final RuntimeException _exception) {
        ReactorImpl reactor = _requestImpl.getTargetReactorImpl();
        reactor.error("runtime exception -> reactor close", _exception);
        try {
            reactor.fail("message timeout");
        } catch (Exception e) {

        }
    }
}
