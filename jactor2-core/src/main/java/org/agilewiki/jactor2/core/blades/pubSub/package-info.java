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
 *             RequestBus<String> requestBus =
 *                 new RequestBus<String>(reactor);
 *             new SubscribeAReq<String>(requestBus, reactor) {
 *                 {@literal @}Override
 *                 protected void processContent(String _content, AsyncResponseProcessor<Void> _asyncResponseProcessor)
 *                         throws Exception {
 *                     System.out.println("got " + _content);
 *                     _asyncResponseProcessor.processAsyncResponse(null);
 *                 }
 *             }.call();
 *             new SubscribeAReq<String>(requestBus, reactor, new EqualsFilter<String>("ribit")) {
 *                 {@literal @}Override
 *                 protected void processContent(String _content, AsyncResponseProcessor<Void> _asyncResponseProcessor)
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
