/**
 * Immutible data structures can not be changed in any way, while supporting the creation of similar structures.
 * Immutable data structures then have three characteristics:
 * <ol>
 *     <li>
 * Methods to modify the structure are not supported. The term used for this is unmodifiable.
 *     </li>
 *     <li>
 * The data structure is not a view of another data structure that can be modified.
 *     </li>
 *     <li>
 * Methods are supported for creating virtual copies of the data structure that differ from the original in some way.
 *     </li>
 * </ol>
 * <p>
 * Modifying an immutable data structure is typically done by creating a similar virtual copy and then updating
 * the reference to that structure. Any code which copied the reference to the original data structure then is
 * unaffected by the switch to the new structure.
 * </p>
 * <p>
 *     Immutable data structures are threadsafe. But their iterators may not be. This is not usually an issue
 *     however, as iterators are rarely if ever used by more than one thread at a time.
 * </p>
 * <p>
 *     Implementing immutable data structures is difficult. Any substructures common to the original and the virtual
 *     copy need to be used by both, while any substructures not used by the virtual copy should be available for
 *     garbage collection when there are no references remaining to the original data structure.
 * </p>
 */
package org.agilewiki.jactor2.modules.immutable;
