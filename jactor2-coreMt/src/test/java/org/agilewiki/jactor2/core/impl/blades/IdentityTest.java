package org.agilewiki.jactor2.core.impl.blades;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeMonad;
import org.agilewiki.jactor2.core.blades.MonadicFunction;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class IdentityTest extends TestCase {
    public void testI() throws Exception {
        MonadicFunction<String> good = new MonadicFunction<String>() {
            @Override
            public String f(BladeMonad<String> source, BladeMonad<String> target) {
                return "good " + source.getImmutable();
            }
        };

        MonadicFunction<String> more = new MonadicFunction<String>() {
            @Override
            public String f(BladeMonad<String> source, BladeMonad<String> target) {
                return "more " + source.getImmutable();
            }
        };

        new Plant();
        final Reactor reactor = new NonBlockingReactor();
        try {
            BladeMonad m = new BladeMonad<String>(reactor, "fun");
            System.out.println(m.getImmutable());
            System.out.println(m.bind(good).evalAReq().call());
            System.out.println(m.getImmutable());
            m = new BladeMonad<String>(reactor, "fun");
            System.out.println(m.bind(good).bind(more).evalAReq().call());
            System.out.println(m.getImmutable());
        } finally {
            Plant.close();
        }
    }
}
