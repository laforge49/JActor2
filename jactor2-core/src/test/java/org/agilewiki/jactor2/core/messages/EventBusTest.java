package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class EventBusTest extends TestCase {
    public void test() throws Exception {
        Facility facility = new Facility();
        try {
            EventBus p = new EventBus(new NonBlockingReactor(facility));
            Printer a = new Printer();
            a.initialize(new NonBlockingReactor(facility));
            a.setName("a");
            p.subscribeAReq(a).call();
            Printer b = new Printer();
            b.initialize(new NonBlockingReactor(facility));
            b.setName("b");
            p.subscribeAReq(b).call();
            Printer c = new Printer();
            c.initialize(new NonBlockingReactor(facility));
            c.setName("c");
            p.subscribeAReq(c).call();
            p.publishAReq(new Print("42")).call();
            p.publishAReq(new Print("24")).call();
            p.publishAReq(new Print("Hello world!")).call();
        } finally {
            facility.close();
        }
    }
}

class Printer extends BladeBase {
    /**
     * The name, or null.
     */
    private String name;

    /**
     * Returns the immutable name.
     *
     * @return The name, or null.
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns a name, unless already assigned.
     *
     * @param _name The blade name.
     */
    public void setName(final String _name) throws Exception {
        if (name != null)
            throw new UnsupportedOperationException("Already named: " + name);
        name = _name;
    }

    public void print(String s) throws Exception {
        System.out.println(getName() + " received " + s);
    }
}

class Print extends Event<Printer> {
    final String msg;

    Print(final String _msg) {
        msg = _msg;
    }

    @Override
    protected void processEvent(final Printer _targetBlade) throws Exception {
        _targetBlade.print(msg);
    }
}