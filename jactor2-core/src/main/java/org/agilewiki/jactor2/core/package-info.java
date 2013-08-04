/**
 * <h1>Actors, Mailboxes, Context and Messaging</h1>
 * <p>
 *     The JActor architecture is divided into 4 parts: actors, mailboxes, context and messaging.
 * </p>
 * <h2>Actors</h2>
 * <p>
 *     Every actor has a mailbox which processes the messages sent to that actor. Thread safety
 *     is achieved by the actor's mailbox, by processing only one message at a time.
 * </p>
 * <p>
 *     To be an actor, an object need only implement the Actor interface. And the Actor
 *     interface has a single method, getMailbox(), that returns the actor's mailbox.
 * </p>
 * <p>
 *     More than one actor can share the same mailbox. And because a mailbox processes only
 *     one message at a time, actors which share a mailbox can safely call each other's
 *     methods. So the actors which share the same mailbox can be thought of as being part of
 *     a composite actor.
 * </p>
 * <h2>Mailboxes</h2>
 * <p>
 *     A mailbox receives and processes the messages for one or more actors, processing these
 *     messages one at a time. A mailbox can also buffer messages being sent to other actors,
 *     and then sends all the messages for each destination as a group to reduce the overhead
 *     of message passing.
 * </p>
 * <p>
 *     A mailbox has an inbox, which is a queue of received messages, and a set of outboxes
 *     used to buffer outgoing messages.
 * </p>
 * <h2>Context</h2>
 * <p>
 *     A context is used to assign threads to mailboxes that have messages to be processed,
 *     with at most one thread being assigned to any given mailbox.
 * </p>
 * <p>
 *     There can be more than one context within a program--which is important when working
 *     with OSGi. Each context has a property set and an independent lifecycle.
 * </p>
 * <h2>Messaging</h2>
 * <p>
 *     Both 1-way messages (events) and 2-way messages (request/response) are supported.
 *     Uncaught exceptions which occur while processing a request are also returned in a
 *     response by default.
 * </p>
 * <p>
 *     All messages are placed in the mailboxes inbox when received and are processed
 *     in the order received. But there are two exceptions to this. First, messages from
 *     actors with the same mailbox are given preference over messages from actors with
 *     different mailboxes.
 *     </p>
 *     <p>
 *     The second exception to message ordering is when an atomic mailbox is used, as an
 *     atomic mailbox gives preference to events and responses over requests.
 * </p>
 */
package org.agilewiki.jactor2.core;