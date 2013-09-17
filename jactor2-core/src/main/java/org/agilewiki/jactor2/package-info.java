/**
 * <h1>Programming for Performance</h1>
 * <p>
 *     Writing software that makes effective use of today's hardware can be quite a challenge.
 *     But the key to doing this is quite simple--as much as possible, keep the data being
 *     used by the thread in that thread's high-speed cache. To do this you need to avoid
 *     locks and avoid passing data between threads. This way the CPU can execute without
 *     having to wait for the data to be loaded and without having that data updated by other threads.
 * </p>
 * <p>
 *     Locks need to be avoided because when a thread blocks, there is no telling when it will
 *     resume execution. And when execution is resumed it may be on a different hardware
 *     thread with a different high-speed cache. But even if it is the same hardware thread,
 *     the cache may now have different data in it.
 * </p>
 * <p>
 *     Passing data between threads is also a slow operation because the data will likely be passed
 *     to a thread running on a different
 *     hardware thread. So the data will need to be loaded into the new hardware thread's high-speed
 *     cache. Of course when you do need to pass data between threads, you should pass the
 *     data in large chunks to minimize the passing overhead.
 * </p>
 * <p>
 *     Another major loss of speed occurs when a thread accesses data being updated by another hardware thread.
 *     When this happens, the contents of the high speed caches for different hardware threads
 *     are synchronized, and that causes additional delays.
 * </p>
 * <h2>Programming with Actors</h2>
 * <p>
 *     One fairly easy way to avoid the use of locks is by using actors. Actors operate by
 *     processing messages passed to them with one simple rule: Only one message is processed
 *     at a time. The result is that message processing is thread-safe.
 * </p>
 * <p>
 *     The problem with actors is that they typically do not avoid passing data between
 *     threads. This is why JActor uses thread migration. So whenever possible, the
 *     thread which creates a message will follow that message to the actor receiving it.
 *     This way the message remains in the high-speed cache of the hardware thread that is
 *     being used.
 * </p>
 * <p>
 *     Unfortunately a thread can not always follow a message, so a second technique is used
 *     as well. Messages are often not sent immediately but are buffered, with a separate
 *     buffer for each destination. These buffers are then flushed when the actor is idle,
 *     with the messages being passed to their destinations in chunks, so that some of the
 *     overhead of passing data between threads can be avoided.
 * </p>
 * <h2>Composing Actors for Enhanced Performance</h2>
 * <p>
 *     In JActor, actors are in two parts: Reactors, which contain no application logic, and
 *     Blades. A targetReactor can have any number of blades, and every blade has one targetReactor. Messages
 *     are sourced by and targeted at blades, but it is the reactors which actually exchange and
 *     process those messages.
 * </p>
 * <p>
 *     Each targetReactor is in effect a light-weight thread, processing one message at a time. The blades
 *     of a targetReactor then always operate on the same thread. Message passing between blades in the same
 *     targetReactor then is very fast, because the messages are not passed between threads.
 * </p>
 * <h2>Two-way Messaging Improves Garbage Collection Performance and Performance Under Load</h2>
 * <p>
 *     The standard actor model is event based and leaves flow control to the application developer.
 *     Which means that event flooding is a common occurrence. This gives rise to a large memory footprint,
 *     slow garbage collection and intermittent failures under loaded conditions.
 * </p>
 * <p>
 *     JActor also supports events, but two-way messages are generally preferred. JActor's two-way
 *     messaging uses callbacks to process responses, rather than blocking the thread. Two-way messaging
 *     implicitly implements flow control and keeps the memory foot print small. So everything runs
 *     faster and more reliably under load.
 * </p>
 * <p>
 *     Another benefit of two-way messaging is that exceptions that occur while processing a message can
 *     be sent back to the origin of the message, just as uncaught exceptions occuring in a method are passed up
 *     to the calling method, recursively. So exceptions are more likely to be handled by code that knows what
 *     might have caused the exception rather than by an actor monitor that may not be tracking activities to the
 *     same degree. This improved exception handling makes for simpler code and consequently code that is more
 *     robust over time as maintenance is simplified.
 * </p>
 */
package org.agilewiki.jactor2;