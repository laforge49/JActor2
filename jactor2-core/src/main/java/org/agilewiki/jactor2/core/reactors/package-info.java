/**
 * <p>
 * A reactor is a light-weight thread dedicated to processing requests and responses (requests with a response value).
 * </p>
 * <p>
 * A reactor has an input queue of requests/responses not yet processed and
 * a table of requests/responses to be sent to other reactors.
 * </p>
 * <h3>Related Tutorial Pages</h3>
 * <ul>
 * <li>
 * <a href="../../../../../../tutorials/core/closeable.html" target="_top">Closeable</a>
 * - Assured Close
 * </li>
 * </ul>
 */
package org.agilewiki.jactor2.core.reactors;