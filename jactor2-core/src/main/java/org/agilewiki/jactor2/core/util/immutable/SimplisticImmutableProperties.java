package org.agilewiki.jactor2.core.util.immutable;

import java.util.*;

/**
 * <p>
 * A brute-force implementation of ImmutableProperties.
 * </p>
 * <pre>
 * Sample:
 *
 * public class SimplisticImmutablePropertiesSample {
 *     public static void main(final String[] args) {
 *         ImmutableProperties&lt;String&gt; ip = SimplisticImmutableProperties.empty();
 *         ip = ip.plus("one", "1");
 *         ip = ip.plus("two", "2");
 *         ImmutableProperties&lt;String&gt; ip2 = ip;
 *         ip = ip.plus("three", "3");
 *         System.out.println(ip2.sortedKeySet());
 *         System.out.println(ip.subMap("t").sortedKeySet());
 *     }
 * }
 *
 * Output:
 *
 * [one, two]
 * [three, two]
 * </pre>
 *
 * @param <VALUE> The type of value.
 */
public class SimplisticImmutableProperties<VALUE> implements ImmutableProperties<VALUE> {
    /**
     * Make an empty ImmutableProperties instance.
     *
     * @param <V> The type of value.
     * @return The empty instance.
     */
    public static <V> ImmutableProperties<V> empty() {
        return new SimplisticImmutableProperties<V>();
    }

    /**
     * Make an ImmutableProperties instance with a single key/value pair.
     *
     * @param key   The key to be included.
     * @param value The value to be included.
     * @param <V>   The type of value.
     * @return The instance with one key/value pair.
     */
    public static <V> ImmutableProperties<V> singleton(String key, V value) {
        return new SimplisticImmutableProperties<V>(key, value);
    }

    /**
     * Make an ImmutableProperties instance that includes a copy of a map.
     *
     * @param m   The map to be included.
     * @param <V> The type of value.
     * @return The instance that includes the map.
     */
    public static <V> ImmutableProperties<V> from(Map<String, V> m) {
        return new SimplisticImmutableProperties<V>(m);
    }

    private final SortedMap<String, VALUE> base;

    private SimplisticImmutableProperties() {
        base = Collections.unmodifiableSortedMap(new TreeMap<String, VALUE>());
    }

    private SimplisticImmutableProperties(String key, VALUE value) {
        TreeMap<String, VALUE> tm = new TreeMap<String, VALUE>();
        tm.put(key, value);
        base = Collections.unmodifiableSortedMap(tm);
    }

    private SimplisticImmutableProperties(Map<String, VALUE> m) {
        base = Collections.unmodifiableSortedMap(new TreeMap<String, VALUE>(m));
    }

    private SimplisticImmutableProperties(SortedMap<String, VALUE> immutableMap) {
        base = Collections.unmodifiableSortedMap(immutableMap);
    }

    @Override
    public ImmutableProperties<VALUE> minus(String key) {
        TreeMap<String, VALUE> tm = new TreeMap<String, VALUE>(base);
        tm.remove(key);
        return new SimplisticImmutableProperties<VALUE>(tm);
    }

    @Override
    public ImmutableProperties<VALUE> plus(String key, VALUE value) {
        TreeMap<String, VALUE> tm = new TreeMap<String, VALUE>(base);
        tm.put(key, value);
        return new SimplisticImmutableProperties<VALUE>(tm);
    }

    @Override
    public ImmutableProperties<VALUE> plusAll(Map<String, VALUE> m) {
        TreeMap<String, VALUE> tm = new TreeMap<String, VALUE>(base);
        tm.putAll(m);
        return new SimplisticImmutableProperties<VALUE>(tm);
    }

    @Override
    public ImmutableProperties<VALUE> subMap(String keyPrefix) {
        return new SimplisticImmutableProperties<VALUE>(base.subMap(keyPrefix, keyPrefix + Character.MAX_VALUE));
    }

    @Override
    public VALUE get(Object key) {
        return base.get(key);
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return base.containsKey(key);
    }

    @Override
    public SortedSet<String> sortedKeySet() {
        return Collections.unmodifiableSortedSet(new TreeSet<String>(base.keySet()));
    }

    @Override
    public Set<String> keySet() {
        return sortedKeySet();
    }

    @Override
    public Collection<VALUE> values() {
        return Collections.unmodifiableCollection(base.values());
    }

    @Override
    public Set<Entry<String, VALUE>> entrySet() {
        return Collections.unmodifiableSet(base.entrySet());
    }

    @Override
    public boolean containsValue(Object value) {
        return base.containsValue(value);
    }

    @Deprecated
    public VALUE put(String key, VALUE value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public VALUE remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void putAll(Map<? extends String, ? extends VALUE> m) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
