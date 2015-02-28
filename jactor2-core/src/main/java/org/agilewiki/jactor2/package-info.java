/**
 * <h3>Why JActor2?</h3>
 * <p>More and more, programs are mandated to use multiple threads. But there are issues...</p>
 * <ul>
 *     <li>High latency when passing data between threads.</li>
 *     <li>Race conditions are difficult to identify.</li>
 *     <li>Deadlocks can occur, unless you use something like
 *     <a href="http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/util/concurrent/CycleDetectingLockFactory.html">CycleDetectingLockFactory</a>.</li>
 * </ul>
 * <p>Of course, there are alternatives to threads and locks. A popular alternative being actors.
 * But there are still issues:</p>
 * <ul>
 *     <li>Actors employ asynchronous messaging,
 *     so there is no assurance that a response will be returned for a request.
 *     and timeouts tend to create additional load when the failure is due to high load.
 *     So the resulting code tends to be complex.</li>
 *     <li>Datalocks can still occur, and there is no equivalent to CycleDetectingLockFactory
 *     for actors.</li>
 *     <li>Passing messages still comes with high latency, so small actors are discouraged.</li>
 * </ul>
 * <p>JActor2 addresses all these issues,
 * making it easy to write robust, high-performance software that scales vertically.</p>
 * <ul>
 *     <li>A <code>Blade</code> in JActor2 is a kind of actor, and processes one message at a time.
 *     So there are no race conditions.</li>
 *     <li>Threads usually follow messages to maximize the use of thread cache. So most of the time
 *     messages are NOT passed between threads. So the use of small <code>Blade</code>s is encouraged.</li>
 *     <li>JActors's <code>IsolationReactor</code>s enforce a partial ordering when message passing,
 *     making it easy to detect potential deadlocks during testing.</li>
 *     <li>The processing of a message, exclusive of the requests sent by that message, is expected to
 *     be done in a given amount of time as specified by the message itself. This is one assurance that
 *     there will be a response to any given request.</li>
 *     <li>But because we distinguish requests from responses,
 *     and track which requests are still outstanding when processing a message,
 *     if message processing completes without sending a response and there are no outstanding requests,
 *     then it can generally be assumed that there is an error.</li>
 * </ul>
 * <p>for more information, see the JActor2
 * <a href="https://github.com/laforge49/JActor2#readme" target="_blank">README</a> page.</p>
 */
package org.agilewiki.jactor2;