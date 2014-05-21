package org.agilewiki.jactor2.core.impl.mtPlant;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

import java.util.*;

/**
 * Implements ISMap.
 */
public class ISMapImpl<VALUE> implements ISMap<VALUE> {

    /**
     * Make an empty ImmutableProperties instance.
     *
     * @return The empty instance.
     */
    public static <V> ISMapImpl<V> empty() {
        return new ISMapImpl<V>();
    }

    /**
     * Make an ImmutableProperties instance with a single key/value pair.
     *
     * @param key The key to be included.
     * @param value The value to be included.
     * @return The instance with one key/value pair.
     */
    public static <V> ISMapImpl<V> singleton(String key, V value) {
        return new ISMapImpl<V>(key, value);
    }

    /**
     * Make an ImmutableProperties instance that includes a copy of a map.
     *
     * @param m      The map to be included.
     * @param <V>    The type of value.
     * @return The instance that includes the map.
     */
    public static <V> ISMapImpl<V> from(Map<String, V> m) {
        return new ISMapImpl<V>(m);
    }

    private HashPMap<String, VALUE> base;

    private ISMapImpl() {
        base = HashTreePMap.empty();
    }

    private ISMapImpl(String _key, VALUE _value) {
        base = HashTreePMap.singleton(_key, _value);
    }

    private ISMapImpl(Map<String, VALUE> _m) {
        base = HashTreePMap.from(_m);
    }

    private ISMapImpl(HashPMap<String, VALUE> immutableMap) {
        base = immutableMap;
    }

    @Override
    public ISMapImpl minus(String key) {
        return new ISMapImpl(base.minus(key));
    }

    @Override
    public ISMapImpl plus(String key, VALUE value) {
        return new ISMapImpl(base.plus(key, value));
    }

    @Override
    public ISMapImpl plusAll(Map<String, VALUE> m) {
        return new ISMapImpl(base.plusAll(m));
    }

    @Override
    public ISMapImpl subMap(String keyPrefix) {
        HashPMap<String, VALUE> hpm = HashTreePMap.empty();
        Iterator<Entry<String, VALUE>> it = base.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, VALUE> e = it.next();
            if (e.getKey().startsWith(keyPrefix))
                hpm = hpm.plus(e.getKey(), e.getValue());
        }
        return new ISMapImpl(hpm);
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
