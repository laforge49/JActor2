/**
 * <h1>Programming for Performance</h1>
 * <p>
 *     Writing software that makes effective use of today's hardware can be quite a challenge.
 *     But the key to doing this is quite simple--as much as possible, keep the data being
 *     used by the thread in that thread's high-speed cache. To do this you need to avoid
 *     locks and avoid passing data between threads. This way the CPU can execute without
 *     having to wait for the data to be loaded.
 * </p>
 * <p>
 *     Locks need to be avoided because when a thread blocks, there is no telling when it will
 *     resume execution. And when execution is resumed it may be on a different hardware
 *     thread with a different high-speed cache. But even if it is the same hardware thread,
 *     the cache may now have different data in it.
 * </p>
 * <p>
 *     Passing data between threads is also a slow operation. First, because the thread receiving
 *     the data may have been idle and must wait for a hardware thread to become available.
 *     Second, because the data will likely be passed to a thread running on a different
 *     hardware thread, so the data will need to be loaded into the new hardware thread's high-speed
 *     cache. Of course when you do need to pass data between threads, you should pass the
 *     data in large chunks to minimize the passing overhead.
 * </p>
 * <h2>Programming with Actors</h2>
 * <p>
 *     One fairly easy way to avoid the use of locks is by using actors. Actors operate by
 *     processing messages passed to them with one simple rule: Only one message is processed
 *     at a time. The result is that message processing is thread-safe.
 * </p>
 * <p>
 *     The problem with actors is that they typically do not avoid passing data between
 *     threads. This is why we use thread migration. Simply put, whenever possible, the
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
 * <h2>Messaging is Not Always Needed</h2>
 * <p>
 *     It is often helpful to use small actors, but the overhead of messaging passing can
 *     be prohibitive. This is why mailboxes are first-class objects.
 * </p>
 * <p>
 *     Actors need an input queue (an inbox) to receive messages from other actors, as well
 *     as buffers to hold unsent messages destined for other actors (outboxes). By allowing
 *     several actors to share the same processing, i.e. inbox and outboxes, these actors
 *     then are effectively part of a larger, composite actor. Such actors can directly
 *     call methods on each other with complete thread safety, as only one message is processed
 *     at a time for all the actors sharing the same processing.
 * </p>
 */
package org.agilewiki.jactor2;