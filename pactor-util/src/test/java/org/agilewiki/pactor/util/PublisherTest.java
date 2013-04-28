package org.agilewiki.pactor.util;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.ActorBase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.api.Transport;
import org.agilewiki.pactor.api.UnboundRequestBase;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

public class PublisherTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            Publisher p = new Publisher();
            p.initialize(mailboxFactory.createMailbox());
            Printer a = new Printer();
            a.initialize(mailboxFactory.createMailbox());
            a.setName("a");
            p.subscribeReq(a).call();
            Printer b = new Printer();
            b.initialize(mailboxFactory.createMailbox());
            b.setName("b");
            p.subscribeReq(b).call();
            Printer c = new Printer();
            c.initialize(mailboxFactory.createMailbox());
            c.setName("c");
            p.subscribeReq(c).call();
            p.publishReq(new Print("42")).call();
            p.publishReq(new Print("24")).call();
            p.publishReq(new Print("Hello world!")).call();
        } finally {
            mailboxFactory.close();
        }
    }
}

class Printer extends ActorBase implements Named {
    /**
     * The name, or null.
     */
    private String name;

    /**
     * Returns the immutable name.
     *
     * @return The name, or null.
     */
    @Override
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

class Print extends UnboundRequestBase<Void, Printer> {
    final String msg;

    Print(final String _msg) {
        msg = _msg;
    }

    @Override
    public void processRequest(final Printer _targetActor,
                               final Transport<Void> _rp) throws Exception {
        _targetActor.print(msg);
        _rp.processResponse(null);
    }
}