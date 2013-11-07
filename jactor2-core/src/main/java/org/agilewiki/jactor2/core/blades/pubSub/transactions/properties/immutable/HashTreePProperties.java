package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable;

import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

import java.util.*;

public class HashTreePProperties<VALUE> implements ImmutableProperties<VALUE> {

    public static <V> ImmutableProperties<V> empty() {
        return new HashTreePProperties<V>();
    }

    public static <V> ImmutableProperties<V> singleton(String key, V value) {
        return new HashTreePProperties<V>(key, value);
    }

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
    public VALUE put(String key, VALUE value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VALUE remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends VALUE> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
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
        return Collections.unmodifiableSet(new TreeSet<Entry<String, VALUE>>(base.entrySet()));
    }
}
