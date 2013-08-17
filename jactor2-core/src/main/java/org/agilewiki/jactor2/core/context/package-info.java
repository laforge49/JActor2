/**
 * <h1>Message Processing Context</h1>
 * <p>
 * A JAContext instance provides an operating context for any number
 * of MessageProcessor instances, with JAContext instance managing the pool
 * of threads used by those MessageProcessor instances.
 * </p>
 * <p>
 *     Each instance of JAContext has an independent lifecycle. Once
 *     created, it can be used in the creation of one or more MessageProcessor
 *     instances. And when a JAContext instance is closed, those
 *     MessageProcessor instances are also closed. Indeed, any object
 *     that implements AutoClosable can be added to or removed from the list of objects
 *     to be closed that is maintained by each instance of JAContext.
 * </p>
 * <p>
 *     JAContext instances do not interact directly. But actors pass messages to other
 *     actors which may or may not be part of a different context. And when a request message from one
 *     context is passed to another context that was or becomes closed, a ServiceClosedException
 *     is be raised in the source actor.
 * </p>
 * <p>
 *     Each instance of JAContext also maintains a table of properties.
 * </p>
 */
package org.agilewiki.jactor2.core.context;