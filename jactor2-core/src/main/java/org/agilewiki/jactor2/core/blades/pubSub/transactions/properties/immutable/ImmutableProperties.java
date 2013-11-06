package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface ImmutableProperties<VALUE> {
    ImmutableProperties<VALUE> minus(String key);
    ImmutableProperties<VALUE> minusAll(Collection<String> keys);
    ImmutableProperties<VALUE> plus(String keys, VALUE value);
    ImmutableProperties<VALUE> plusAll(Map<String, VALUE> m);
    ImmutableProperties<VALUE> subMap(String keyPrefix);
    VALUE get(String key);
    boolean containsKey(String key);
    Set<String> keySet();
    SortedSet<String> sortedKeySet();
    int size();
    boolean isEmpty();
}
