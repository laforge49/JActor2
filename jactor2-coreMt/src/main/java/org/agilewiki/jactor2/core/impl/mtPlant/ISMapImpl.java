package org.agilewiki.jactor2.core.impl.mtPlant;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

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
    public static <V> ISMapImpl<V> singleton(final String key, final V value) {
        return new ISMapImpl<V>(key, value);
    }

    /**
     * Make an ImmutableProperties instance that includes a copy of a map.
     *
     * @param m      The map to be included.
     * @param <V>    The type of value.
     * @return The instance that includes the map.
     */
    public static <V> ISMapImpl<V> from(final Map<String, V> m) {
        return new ISMapImpl<V>(m);
    }

    private final HashPMap<String, VALUE> base;

    private ISMapImpl() {
        base = HashTreePMap.empty();
    }

    private ISMapImpl(final String _key, final VALUE _value) {
        base = HashTreePMap.singleton(_key, _value);
    }

    private ISMapImpl(final Map<String, VALUE> _m) {
        base = HashTreePMap.from(_m);
    }

    private ISMapImpl(final HashPMap<String, VALUE> immutableMap) {
        base = immutableMap;
    }

    @Override
    public ISMapImpl minus(final String key) {
        return new ISMapImpl(base.minus(key));
    }

    @Override
    public ISMapImpl plus(final String key, final VALUE value) {
        return new ISMapImpl(base.plus(key, value));
    }

    @Override
    public ISMapImpl plusAll(final Map<String, VALUE> m) {
        return new ISMapImpl(base.plusAll(m));
    }

    @Override
    public ISMapImpl subMap(final String keyPrefix) {
        HashPMap<String, VALUE> hpm = HashTreePMap.empty();
        final Iterator<Entry<String, VALUE>> it = base.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, VALUE> e = it.next();
            if (e.getKey().startsWith(keyPrefix)) {
                hpm = hpm.plus(e.getKey(), e.getValue());
            }
        }
        return new ISMapImpl(hpm);
    }

    @Override
    public VALUE get(final Object key) {
        return base.get(key);
    }

    @Override
    public boolean containsKey(final Object key) {
        return base.containsKey(key);
    }

    @Override
    public SortedSet<String> sortedKeySet() {
        return Collections.unmodifiableSortedSet(new TreeSet<String>(base
                .keySet()));
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
    public boolean containsValue(final Object value) {
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

    @Override
    @Deprecated
    public VALUE put(final String key, final VALUE value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public VALUE remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void putAll(final Map<? extends String, ? extends VALUE> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
