/**
 * Core provides a framework that significantly reduces the cost of
 * developing robust asynchronous applications (beta).
 * This is because of the assurances made by core:
 * <ol>
 *     <li>
 * A Closeable object will be closed if it has been registered with one or more
 * reactors.
 *     </li>
 *     <li>
 * An asynchronous request will receive a response, even if the target of the request
 * fails.
 *     </li>
 * </ol>
 */
package org.agilewiki.jactor2.core;