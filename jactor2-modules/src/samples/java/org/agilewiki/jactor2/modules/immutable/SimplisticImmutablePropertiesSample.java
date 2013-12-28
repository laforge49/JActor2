package org.agilewiki.jactor2.modules.immutable;

import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.core.util.immutable.SimplisticImmutableProperties;

public class SimplisticImmutablePropertiesSample {
    public static void main(final String[] args) {
        ImmutableProperties<String> ip = SimplisticImmutableProperties.empty();
        ip = ip.plus("one", "1");
        ip = ip.plus("two", "2");
        ImmutableProperties<String> ip2 = ip;
        ip = ip.plus("three", "3");
        System.out.println(ip2.sortedKeySet());
        System.out.println(ip.subMap("t").sortedKeySet());
    }
}
