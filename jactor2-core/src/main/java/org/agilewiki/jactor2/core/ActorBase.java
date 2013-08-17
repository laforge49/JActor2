package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * <p>
 * ActorBase is a convenience class that implements an Actor. Initialization is not
 * thread-safe, so it should be done before a reference to the actor is shared.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorBaseSample extends ActorBase {
 *     public ActorBaseSample(final MessageProcessor _processor) throws Exception {
 *         initialize(_processor);
 *     }
 * }
 * </pre>
 */
public class ActorBase implements Actor {
    /**
     * The actor's message processor.
     */
    private MessageProcessor messageProcessor;

    /**
     * True when initialized, this flag is used to prevent the message processor from being changed.
     */
    private boolean initialized;

    /**
     * Returns true when the actor has been initialized.
     *
     * @return True when the actor has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize an actor. This method can only be called once
     * without raising an illegal state exception, as the message processor
     * can not be changed.
     *
     * @param _messageProcessor The actor's message processor.
     */
    public void initialize(final MessageProcessor _messageProcessor) throws Exception {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        if (_messageProcessor == null)
            throw new IllegalArgumentException("MessageProcessor may not be null");
        initialized = true;
        messageProcessor = _messageProcessor;
    }

    @Override
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }
}
