package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

public class EventBusTest extends TestCase {
    public void test() throws Exception {
        JAContext jaContext = new JAContext();
        try {
            EventBus p = new EventBus();
            p.initialize(new NonBlockingMessageProcessor(jaContext));
            Printer a = new Printer();
            a.initialize(new NonBlockingMessageProcessor(jaContext));
            a.setName("a");
            p.subscribeReq(a).call();
            Printer b = new Printer();
            b.initialize(new NonBlockingMessageProcessor(jaContext));
            b.setName("b");
            p.subscribeReq(b).call();
            Printer c = new Printer();
            c.initialize(new NonBlockingMessageProcessor(jaContext));
            c.setName("c");
            p.subscribeReq(c).call();
            p.publishReq(new Print("42")).call();
            p.publishReq(new Print("24")).call();
            p.publishReq(new Print("Hello world!")).call();
        } finally {
            jaContext.close();
        }
    }
}

class Printer extends ActorBase {
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
     * @param _name The actor name.
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
    public void processEvent(final Printer _targetActor) throws Exception {
        _targetActor.print(msg);
    }
}