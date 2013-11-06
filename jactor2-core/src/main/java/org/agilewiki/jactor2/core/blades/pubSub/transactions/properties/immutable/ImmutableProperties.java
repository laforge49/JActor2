package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface ImmutableProperties<VALUE> extends Map<String, VALUE> {
    ImmutableProperties<VALUE> minus(String key);
    ImmutableProperties<VALUE> plus(String key, VALUE value);
    ImmutableProperties<VALUE> plusAll(Map<String, VALUE> m);
    ImmutableProperties<VALUE> subMap(String keyPrefix);
    VALUE get(String key);
    boolean containsKey(String key);
    SortedSet<String> sortedKeySet();
    int size();
    boolean isEmpty();
}
