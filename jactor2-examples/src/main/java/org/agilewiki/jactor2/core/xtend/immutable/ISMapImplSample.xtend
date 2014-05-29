package org.agilewiki.jactor2.core.xtend.immutable;

import org.agilewiki.jactor2.core.impl.mtPlant.ISMapImpl

public class ISMapImplSample {
    def static void main(String[] args) {
        var ip = ISMapImpl.empty();
        ip = ip.plus("one", "1");
        ip = ip.plus("two", "2");
        val ip2 = ip;
        ip = ip.plus("three", "3");
        System.out.println(ip2.sortedKeySet());
        System.out.println(ip.subMap("t").sortedKeySet());
    }
}
