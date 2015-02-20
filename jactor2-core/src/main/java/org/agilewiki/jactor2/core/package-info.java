/**
 * <p>The JActor2 framework is built from 4 types of things:</p>
 * <ul>
 *     <li>
 * Plant - A singleton that holds the threadpool and handles things like recovery and configuration.
 *     </li>
 *     <li>
 * Reactors - Light-weight threads or fibers that manage and dispatch messages.
 *     </li>
 *     <li>
 * Blades - Hold state and the operations which manipulate same.
 *     </li>
 *     <li>
 * Messages - Operations passed between Reactors for processing by Blades.
 *     </li>
 * </ul>
 */
package org.agilewiki.jactor2.core;