package org.agilewiki.jactor2.core.impl.immutable;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.impl.mtPlant.ISMapImpl;

public class ISMapImplSample {
    public static void main(final String[] args) {
        ISMap<String> ip = ISMapImpl.empty();
        ip = ip.plus("one", "1");
        ip = ip.plus("two", "2");
        ISMap<String> ip2 = ip;
        ip = ip.plus("three", "3");
        System.out.println(ip2.sortedKeySet());
        System.out.println(ip.subMap("t").sortedKeySet());
    }
}
