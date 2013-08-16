/**
 * <h1>Message Processing Context</h1>
 * <p>
 * The JAContext class provides an operating context for any number
 * of MessageProcessor instances, with JAContext managing the pool
 * of threads used by MessageProcessor.
 * </p>
 * <p>
 *     Each instance of JAContext has an independent lifecycle. Once
 *     created, it can be used in the creation of one or more MessageProcessor
 *     instances. And when a JAContext instance is closed, those
 *     MessageProcessor instances are also closed. Indeed, any object
 *     that implements AutoClosable can be added to the list of objects
 *     to be closed that is maintained by each instance of JAContext.
 * </p>
 * <p>
 *     Each instance of JAContext also maintains a table of properties.
 * </p>
 */
package org.agilewiki.jactor2.core.context;