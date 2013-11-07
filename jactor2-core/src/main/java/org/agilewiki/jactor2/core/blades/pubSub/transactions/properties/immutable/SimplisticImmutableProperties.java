package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable;

import java.util.*;

public class SimplisticImmutableProperties<VALUE> implements ImmutableProperties<VALUE> {

    public static <V> ImmutableProperties<V> empty() {
        return new SimplisticImmutableProperties<V>();
    }

    public static <V> ImmutableProperties<V> singleton(String key, V value) {
        return new SimplisticImmutableProperties<V>(key, value);
    }

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
    public VALUE get(String key) {
        return base.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return base.containsKey(key);
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
    public VALUE get(Object key) {
        return base.get(key);
    }

    @Override
    public SortedSet<String> sortedKeySet() {
        return Collections.unmodifiableSortedSet(new TreeSet<String>(base.keySet()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<VALUE> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, VALUE>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
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
}
