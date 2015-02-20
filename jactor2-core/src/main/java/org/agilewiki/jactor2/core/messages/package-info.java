/**
 * <p>
 * A request is a single-use object for performing an operation safely and to optionally be passed back with a response
 * value that is also processed safely (beta).
 * </p>
 * <p>
 * There are two types of requests:
 * </p>
 * <ol>
 * <li>
 * A sync request performs an operation safely within the thread context of the target reactor.
 * And
 * </li>
 * <li>
 * An async request separates data flow from control flow and its effect can span multiple reactors.
 * </li>
 * </ol>
 * <p>
 * All types of requests can be passed to a target reactor using either the
 * <a href="Request.html#call--">call</a>
 * or
 * <a href="Request.html#signal--">signal</a>
 * methods.
 * And when operating within the context of an async request, other requests can be passed to a target reactor using the
 * <a href="AsyncRequest.html#send-org.agilewiki.jactor2.core.requests.Request-org.agilewiki.jactor2.core.requests.AsyncResponseProcessor-">send</a>
 * method.
 * </p>
 * <h3>Related Tutorial Pages</h3>
 * <ul>
 * <li>
 * <a href="../../../../../../tutorials/core/ponger.html" target="_top">Ponger</a>
 * - SyncRequest
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/callSpeedReport.html" target="_top">CallSpeedReport</a>
 * - Call Methods per Second
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/pongerLoop.html" target="_top">PongerLoop</a>
 * - Direct Method calls per Second
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/foreignPing.html" target="_top">ForeignPing</a>
 * - AsyncRequest
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/pinger.html" target="_top">Pinger</a>
 * - Asynchronous Loops
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/batcher.html" target="_top">Batcher</a>
 * - Batched Messages per Second
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/parallel.html" target="_top">Parallel</a>
 * - Parallel Processing
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/exceptionHandler.html" target="_top">ExceptionHandler</a>
 * - Beyond try/catch
 * </li>
 * <li>
 * <a href="../../../../../../tutorials/core/signals.html" target="_top">Signals</a>
 * - One-way Messages
 * </li>
 * </ul>
 */
package org.agilewiki.jactor2.core.messages;