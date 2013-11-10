/**
 * <p>
 * Support is provided for non-durable (in-memory) transactions.
 * </p>
 * <ol>
 *     <li>
 * Atomicity - State is not updated if an exception is thrown.
 *     </li>
 *     <li>
 * Consistency - On completion of a transaction, the proposed change to the state is sent to
 * all the subscribing validators. If any of the validators throws an exception, the state is not updated.
 *     </li>
 *     <li>
 * Isolation - Isolation is provided by an IsolationReactor. Each transaction request is processed to
 * completion before the next request is processed.
 *     </li>
 * </ol>
 * <p>
 *     Two RequestBus instances are used. The first is used to manage the validation subscribers. And the second is
 *     used for notifications.
 * </p>
 * <p>
 *     The state is an immutable data structure, which means the state can be accessed directly
 *     in place of query transactions. On successful processing of a transaction and validation of the change,
 *     the immutable state is replaced by the updated version.
 * </p>
 */
package org.agilewiki.jactor2.core.blades.pubSub.transactions;
