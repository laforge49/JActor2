/**
 * <h1>Facility</h1>
 * <p>
 * A Facility instance provides the operating facility for any number
 * of Reactor instances, with the Facility instance managing a pool
 * of threads for those Reactor instances.
 * </p>
 * <p>
 *     Each instance of Facility has an independent lifecycle. Once
 *     created, it can be used in the creation of one or more Reactor
 *     instances. And when a Facility instance is closed, those
 *     Reactor instances are also closed. Indeed, any object
 *     that implements AutoClosable can be added to or removed from the list of objects
 *     to be closed that is maintained by each instance of Facility.
 * </p>
 * <p>
 *     Facility instances do not interact directly. But actors pass messages to other
 *     actors which may or may not be part of a different facility. And when a request message from one
 *     facility is passed to another facility that was or becomes closed, a ServiceClosedException
 *     is be raised in the source actor.
 * </p>
 * <p>
 *     Each instance of Facility also maintains a table of properties.
 * </p>
 */
package org.agilewiki.jactor2.core.threading;