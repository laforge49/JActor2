package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.SyncRequest;

/**
 * A convenience class for implementing sync agents.
 */
abstract public class SyncAgentBase<RESPONSE_TYPE, BLADE_TYPE extends Blade>
        extends BladeBase implements SyncAgent {
    /**
     * The blade to which this agent is bound.
     */
    protected BLADE_TYPE blade;

    /**
     * Create a sync agent.
     *
     * @param _blade The blade to which this agent is bound.
     */
    public SyncAgentBase(final BLADE_TYPE _blade) throws Exception {
        initialize(_blade.getReactor());
        blade = _blade;
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
