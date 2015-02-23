/**
 * <p>
 *     Blades hold state and a set of operations which are performed on that state.
 * A blade is initialized when it has been assigned a reactor.
 * (Any number of blades can share the same reactor.)
 * </p>
 * <p>
 *     BladeBase provides a number of useful methods, but is not required for an object to be a blade.
 * </p>
 * <p>
 *     There are a number of specialized blades, depending on the type of reactor they use. On the other
 *     hand, NamedBlade is a blade that has a name and can be registered with a facility.
 * </p>
 */
package org.agilewiki.jactor2.core.blades;