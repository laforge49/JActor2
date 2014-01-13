package org.agilewiki.jactor2.modules.immutable;

import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

import java.util.*;

/**
 * <p>
 * An implementation of ImmutableProperties based on pcollections.
 * </p>
 * <pre>
 * Sample:
 *
 * public class HashTreePPropertiesSample {
 *     public static void main(final String[] args) {
 *         ImmutableProperties&lt;String&gt; ip = HashTreePProperties.empty();
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
public class HashTreePProperties<VALUE> implements ImmutableProperties<VALUE> {

    /**
     * Make an empty ImmutableProperties instance.
     *
     * @param <V> The type of value.
     * @return The empty instance.
     */
    public static <V> ImmutableProperties<V> empty() {
        return new HashTreePProperties<V>();
    }

    /**
     * Make an ImmutableProperties instance with a single key/value pair.
     *
     * @param key The key to be included.
     * @param value The value to be included.
     * @param <V>   The type of value.
     * @return The instance with one key/value pair.
     */
    public static <V> ImmutableProperties<V> singleton(String key, V value) {
        return new HashTreePProperties<V>(key, value);
    }

    /**
     * Make an ImmutableProperties instance that includes a copy of a map.
     *
     * @param m      The map to be included.
     * @param <V>    The type of value.
     * @return The instance that includes the map.
     */
    public static <V> ImmutableProperties<V> from(Map<String, V> m) {
        return new HashTreePProperties<V>(m);
    }

    private HashPMap<String, VALUE> base;

    private HashTreePProperties() {
        base = HashTreePMap.empty();
    }

    private HashTreePProperties(String _key, VALUE _value) {
        base = HashTreePMap.singleton(_key, _value);
    }

    private HashTreePProperties(Map<String, VALUE> _m) {
        base = HashTreePMap.from(_m);
    }

    private HashTreePProperties(HashPMap<String, VALUE> immutableMap) {
        base = immutableMap;
    }

    @Override
    public ImmutableProperties<VALUE> minus(String key) {
        return new HashTreePProperties<VALUE>(base.minus(key));
    }

    @Override
    public ImmutableProperties<VALUE> plus(String key, VALUE value) {
        return new HashTreePProperties<VALUE>(base.plus(key, value));
    }

    @Override
    public ImmutableProperties<VALUE> plusAll(Map<String, VALUE> m) {
        return new HashTreePProperties<VALUE>(base.plusAll(m));
    }

    @Override
    public ImmutableProperties<VALUE> subMap(String keyPrefix) {
        HashPMap<String, VALUE> hpm = HashTreePMap.empty();
        Iterator<Entry<String, VALUE>> it = base.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, VALUE> e = it.next();
            if (e.getKey().startsWith(keyPrefix))
                hpm = hpm.plus(e.getKey(), e.getValue());
        }
        return new HashTreePProperties<VALUE>(hpm);
    }

    @Override
    public VALUE get(Object key) {
        return base.get(key);
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
    public String toString() {
        return new TreeMap<String, Object>(base).toString();
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
    public boolean containsValue(Object value) {
        return base.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        return sortedKeySet();
    }

    @Override
    public Collection<VALUE> values() {
        return base.values();
    }

    @Override
    public Set<Entry<String, VALUE>> entrySet() {
        return base.entrySet();
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
