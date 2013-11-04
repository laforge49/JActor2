package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final Plant plant = new Plant();
        final Reactor reactor = new NonBlockingReactor(plant);
        new Blade4(reactor).hi4SReq().call();
        plant.close();
    }

    public void testd() throws Exception {
        final Plant plant = new Plant();
        new Blade4(new IsolationReactor(plant)).hi4SReq().call();
        plant.close();
    }
}
