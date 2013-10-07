package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

/**
 * A convenience class for implementing async agents.
 */
@Deprecated
abstract public class AsyncAgentBase<RESPONSE_TYPE, BLADE_TYPE extends Blade>
        extends BladeBase implements AsyncAgent {
    /**
     * The blade which is local to this agent.
     */
    protected BLADE_TYPE localBlade;

    /**
     * Create an async agent.
     *
     * @param _localBlade The blade which is to be local to this agent.
     */
    public AsyncAgentBase(final BLADE_TYPE _localBlade) throws Exception {
        initialize(_localBlade.getReactor());
        localBlade = _localBlade;
    }

    @Override
    public AsyncRequest<RESPONSE_TYPE> startAReq() {
        return new AsyncBladeRequest<RESPONSE_TYPE>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                start(this);
            }
        };
    }

    /**
     * The application logic of the agent.
     *
     * @param dis The response processor.
     */
    abstract protected void start(final AsyncResponseProcessor<RESPONSE_TYPE> dis) throws Exception;
}
