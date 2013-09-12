/**
 * <h1>Actors, Processing, Facilities and Messaging</h1>
 * <p>
 *     The JActor architecture is divided into 4 parts: actors, processing, threading and messaging.
 * </p>
 * <h2>Actors</h2>
 * <p>
 *     Every actor has a message processor which processes the messages sent to that actor. Thread safety
 *     for an actor is achieved by processing only one message at a time.
 * </p>
 * <p>
 *     To be an actor, an object need only implement the Actor interface. And the Actor
 *     interface has a single method, getMessageProcessor(), that returns the actor's message processor.
 * </p>
 * <p>
 *     More than one actor can share the same message processor. And because a message processor only processes
 *     one message at a time, actors which share a processing can safely call each other's
 *     methods. So the actors which share the same processing can be thought of as being part of
 *     a larger composite actor.
 * </p>
 * <h2>Processing</h2>
 * <p>
 *     A message processor receives and subsequently processes the messages for one or more actors, processing those
 *     messages one at a time. A message processor can also buffer messages being sent to other actors,
 *     and then sends all the messages for each destination as a group to reduce the overhead
 *     of message passing.
 * </p>
 * <p>
 *     A message processor has an inbox, which is a queue of received messages, and an outbox that has a send buffer
 *     for each destination.
 * </p>
 * <h2>Threading</h2>
 * <p>
 *     A facility is used to assign threads to message processors that have an inbox that is not empty,
 *     with at most one thread being assigned to any given message processor.
 * </p>
 * <p>
 *     There can be more than one facility within a program--which is important when working
 *     with OSGi. Each facility has a property set and an independent lifecycle. When messages are
 *     sent to a facility that is closed, an exception is raised in the sending actor.
 * </p>
 * <p>
 *     Multiple facilities can also be important when a Swing program has multiple frames that
 *     have associated background tasks. If a frame has its own facility that is closed when
 *     the frame is closed, all the background activity associated with that frame is terminated.
 * </p>
 * <h2>Messaging</h2>
 * <p>
 *     Both 1-way messages (events) and 2-way messages (request/response) are supported.
 *     Uncaught exceptions which occur while processing a request are raised in the actor that sent that request.
 * </p>
 * <p>
 *     All messages are placed in the actor's inbox when received and are processed
 *     in the order received. But there are two exceptions to this. First, messages from
 *     actors with the same message processor are given preference over messages from other actors.
 *     </p>
 *     <p>
 *     The second exception to message ordering is when an isolation message processor is used, as an
 *     isolation message processor gives preference to events and responses over requests and in any case does not
 *     process a request until a response to the prior request has been sent.
 * </p>
 */
package org.agilewiki.jactor2.core;