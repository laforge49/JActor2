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
     * Controls how frequently reactors are polled for message timeouts.
     *
     * @return The number of milliseconds between polls. Default = 500.
     */
    public long getReactorPollMillis() {
        return 500;
    }

    /**
     * Determines how long a message can be processed before timing out.
     * Default for Blocking reactors: 5 minutes.
     * Default for all other reactors: 1 second.
     *
     * @param _reactorImpl  The reactor which may have a timed-out message.
     * @return Number of milliseconds.
     */
    public long getMessageTimeoutMillis(final ReactorImpl _reactorImpl) {
        if (_reactorImpl.isSlow())
            return 300000;
        return 1000;
    }

    /**
     * Handles message timeout.
     * Default action: close the reactor.
     *
     * @param _reactorImpl    The reactor with the timed-out message
     */
    public void onMessageTimeout(final ReactorImpl _reactorImpl) throws Exception {
        _reactorImpl.error("message timeout -> reactor close");
        _reactorImpl.fail("message timeout");
    }

    /**
     * Handles hung thread. Default action: close the plant and exit the program.
     *
     * @param _reactorImpl    The reactor whose thread is hung.
     */
    public void onHungThread(final ReactorImpl _reactorImpl) {
        _reactorImpl.error("hung thread -> plant exit");
        try {
            PlantBase.close();
        } catch (Exception ex) {}
        System.exit(10);
    }

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
