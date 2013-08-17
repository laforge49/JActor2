package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * <p>
 * Actors must implement the Actor interface to provide access to their message processor.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorSample implements Actor {
 *     private final MessageProcessor processor;
 *
 *     ActorSample(final MessageProcessor _processor) {
 *         processor = _processor;
 *     }
 *
 *     {@literal @}Override
 *     public final MessageProcessor getMessageProcessor() {
 *         return processor;
 *     }
 * }
 * </pre>
 */
public interface Actor {
    /**
     * Returns the message processor associated with this Actor.
     *
     * @return The actor's message processor.
     */
    public MessageProcessor getMessageProcessor();
}
