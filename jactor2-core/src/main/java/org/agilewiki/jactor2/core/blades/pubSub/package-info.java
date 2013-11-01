/**
 * <p>
 * The pubSub package supports the management of a set of subscriptions and to publish content.
 * Subscriptions are used to select the content of interest and to process that content.
 * </p>
 * <pre>
 * import org.agilewiki.jactor2.core.facilities.Plant;
 * import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
 * import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
 *
 * public class PubSubSample {
 *     public static void main(final String[] args) throws Exception {
 *         final Plant plant = new Plant();
 *         try {
 *             NonBlockingReactor reactor = new NonBlockingReactor(plant);
 *             RequestBus&lt;String&gt; requestBus =
 *                 new RequestBus&lt;String&gt;(reactor);
 *             new SubscribeAReq&lt;String&gt;(requestBus, reactor) {
 *                 {@literal @}Override
 *                 protected void processContent(String _content)
 *                         throws Exception {
 *                     System.out.println("got " + _content);
 *                 }
 *             }.call();
 *             new SubscribeAReq&lt;String&gt;(requestBus, reactor, new EqualsFilter&lt;String&gt;("ribit")) {
 *                 {@literal @}Override
 *                 protected void processContent(String _content, AsyncResponseProcessor&lt;Void&gt; _asyncResponseProcessor)
 *                         throws Exception {
 *                     System.out.println("*** Ribit! ***");
 *                     _asyncResponseProcessor.processAsyncResponse(null);
 *                 }
 *             }.call();
 *             System.out.println("\nPublishing null.");
 *             requestBus.sendsContentAReq(null).call();
 *             System.out.println("\nPublishing ribit");
 *             requestBus.sendsContentAReq("ribit").call();
 *             System.out.println("\nPublishing abc");
 *             requestBus.sendsContentAReq("abc").call();
 *         } finally {
 *             plant.close();
 *         }
 *     }
 * }
 *
 *     Output:
 *
 * Publishing null.
 * got null
 *
 * Publishing ribit
 * ** Ribit! ***
 * got ribit
 *
 * Publishing abc
 * got abc
 * </pre>
 */
package org.agilewiki.jactor2.core.blades.pubSub;
