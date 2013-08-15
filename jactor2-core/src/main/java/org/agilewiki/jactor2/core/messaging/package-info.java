/**
 * <h1>Events, Requests, Responses and Exception Handling</h1>
 * <p>
 *     There are two types of messaging, 1-way (events) and 2-way (request/response).
 * </p>
 * <h2>Event Messages</h2>
 * <p>
 *     Event messages are created and passed using subclasses of the Event class.
 *     An event is not bound to an actor instance, so the same event can be used to
 *     send messages to any number of actors. And when passing an event message to an actor,
 *     the message is given to the target actor's mailbox immediately.
 * </p>
 * <h2>Request/Response Messages</h2>
 * <p>
 *     In contrast, request messages are created and passed using anonymous subclasses of
 *     the Request class, with these anonymous classes present within the actor where the
 *     request message is evaluated. Response messages are simply request messages to which a
 *     response has been assigned. Both request and response messages, when passed from an
 *     actor, are aggregated and sent in groups to reduce the overhead of passing individual
 *     messages.
 * </p>
 * <h2>Exception Handling</h2>
 * <p>
 *     Exception handlers are used to process otherwise uncaught exception. But if there is no
 *     exception handler, or if an exception is thrown by an exception handler, then the
 *     exception is returned as a response, though if the current message being processed is
 *     an event then the exception is simply logged. However, when a response message that
 *     holds an event is processed by the source mailbox, the exception is rethrown rather
 *     than passing it back to the application as a valid response.
 * </p>
 * <p>
 *     Before a mailbox processes an event or a request, the current exception handler is set
 *     to null. The application's message processing logic then has the option of assigning
 *     an exception handler via the Mailbox.setExceptionHandler method. When a request message
 *     is sent, the current exception handler is saved in the request message and subsequently
 *     restored when a response is received.
 * </p>
 */
package org.agilewiki.jactor2.core.messaging;