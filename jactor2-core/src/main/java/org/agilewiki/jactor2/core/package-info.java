/**
 * <p>The JActor2 is organized in 4 parts:</p>
 * <ul>
 *     <li>
 * Plant - A singleton that holds the threadpool and the scheduler,
 * and handles things like recovery and configuration.
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