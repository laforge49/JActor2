package org.agilewiki.jactor2.core.impl.messages;

import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Test code.
 */
public class Test1 extends CallTestBase {
    public void testa() throws Exception {
        new Plant();
        final IsolationReactor reactor = new IsolationReactor();
        final Blade11 blade1 = new Blade11(reactor);
        final String result = call(blade1.hiSOp());
        assertEquals("Hello world!", result);
        Plant.close();
    }
}
