/**
 * <p>
 * Full-featured, lock-free, in-memory immutable transactional properties.
 * </p>
 * <ul>
 *     <li>Filtering of validation and change notifications based on property prefix.</li>
 *     <li>Immutable property map eliminates the need for queries.</li>
 * </ul>
 * <p>
 *     Properties in the immutable properties map may not be null.
 *     Properties assigned a null value are simply removed.
 * </p>
 * <pre>
 * Sample:
 *
 * package org.agilewiki.jactor2.core.blades.transactions.properties;
 *
 * import RequestBus;
 * import SubscribeAReq;
 * import org.agilewiki.jactor2.core.facilities.BasicPlant;
 * import org.agilewiki.jactor2.core.reactors.CommonReactor;
 * import org.agilewiki.jactor2.core.reactors.IsolationReactor;
 * import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
 *
 * import java.util.Iterator;
 * import java.util.SortedMap;
 *
 * public class PropertiesSample {
 * public static void main(final String[] _args) throws Exception {
 *     final BasicPlant plant = new BasicPlant();
 *     try {
 *         PropertiesProcessor propertiesProcessor = new PropertiesProcessor(new IsolationReactor(plant));
 *         final CommonReactor reactor = new NonBlockingReactor(plant);
 *         RequestBus&lt;ImmutablePropertyChanges&gt; validationBus = propertiesProcessor.validationBus;
 *
 *         new SubscribeAReq&lt;ImmutablePropertyChanges&gt;(
 *                  validationBus,
 *                  reactor,
 *                  new PropertyChangesFilter("immutable.")){
 *              {@literal @}Override
 *              protected void processContent(final ImmutablePropertyChanges _content)
 *                      throws Exception {
 *                  SortedMap&lt;String, PropertyChange&gt; readOnlyChanges = _content.readOnlyChanges;
 *                  final Iterator&lt;PropertyChange&gt; it = readOnlyChanges.values().iterator();
 *                  while (it.hasNext()) {
 *                      final PropertyChange propertyChange = it.next();
 *                      if (propertyChange.name.startsWith("immutable.") &amp;&amp; propertyChange.oldValue != null) {
 *                          throw new IllegalArgumentException("Immutable property can not be changed: " +
 *                              propertyChange.name);
 *                      }
 *                  }
 *              }
 *          }.call();
 *
 *          try {
 *              propertiesProcessor.putAReq("pie", "apple").call();
 *              propertiesProcessor.putAReq("pie", "peach").call();
 *              propertiesProcessor.putAReq("pie", null).call();
 *              propertiesProcessor.putAReq("fruit", "pear").call();
 *              propertiesProcessor.putAReq("fruit", "orange").call();
 *              propertiesProcessor.putAReq("immutable.fudge", "fun").call();
 *              propertiesProcessor.putAReq("immutable.fudge", null).call();
 *         } catch (final Exception e) {
 *             System.out.println(e.getMessage());
 *         }
 *             System.out.println(propertiesProcessor.getImmutableState().sortedKeySet());
 *         } finally {
 *             plant.close();
 *         }
 *     }
 * }
 *
 * Output:
 *
 * Immutable property can not be changed: immutable.fudge
 * [fruit, immutable.fudge]
 * </pre>
 */
package org.agilewiki.jactor2.modules.transactions.properties;
