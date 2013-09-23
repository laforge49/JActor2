package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.SyncRequest;

/**
 * A convenience class for implementing sync agents.
 */
abstract public class SyncAgentBase<RESPONSE_TYPE, BLADE_TYPE extends Blade>
        extends BladeBase implements SyncAgent {
    /**
     * The blade which is local to this agent.
     */
    protected BLADE_TYPE localBlade;

    /**
     * Create a sync agent.
     *
     * @param _localBlade The blade which is to be local to this agent.
     */
    public SyncAgentBase(final BLADE_TYPE _localBlade) throws Exception {
        initialize(_localBlade.getReactor());
        localBlade = _localBlade;
    }

    @Override
    public SyncRequest<RESPONSE_TYPE> startSReq() {
        return new SyncBladeRequest<RESPONSE_TYPE>() {
            @Override
            protected RESPONSE_TYPE processSyncRequest() throws Exception {
                return start();
            }
        };
    }

    /**
     * The application logic of the agent.
     *
     * @return The agent's response.
     */
    abstract protected RESPONSE_TYPE start() throws Exception;
}
