package org.agilewiki.jactor.util.fbp;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;

import java.util.HashSet;
import java.util.Iterator;

abstract public class FActor extends Thread implements Actor, AutoCloseable {

    private MailboxFactory mailboxFactory;

    private Mailbox mailbox;

    protected Activity activity;

    protected Idle idle;

    protected HashSet<InPort> inPorts = new HashSet<InPort>();

    protected HashSet<OutPort> outPorts = new HashSet<OutPort>();

    private boolean closed;

    public FActor(final MailboxFactory _mailboxFactory) {
        mailboxFactory = _mailboxFactory;
        mailboxFactory.addAutoClosable(this);
        mailbox = mailboxFactory.createThreadBoundMailbox(new Runnable() {
            @Override
            public void run() {
                interrupt();
            }
        });
        initialize();
        start();
    }

    protected void initialize() {
        idle = new Idle();
        activity = idle;
    }

    @Override
    public void run() {
        while (true) {
            activity.run();
            if (mailboxFactory.isClosing())
                return;
            mailbox.run();
        }
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    abstract public void closed(final InPort _inPort);

    abstract public void closed(final OutPort _outPort);

    @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        Iterator<InPort> inIt = inPorts.iterator();
        while (inIt.hasNext()) {
            try {
                inIt.next().close();
            } catch (Exception e) {
            }
        }
        Iterator<OutPort> outIt = outPorts.iterator();
        while (outIt.hasNext()) {
            try {
                outIt.next().close();
            } catch (Exception e) {
            }
        }
        this.interrupt();
    }

    public void addInPort(final InPort _inPort) {
        inPorts.add(_inPort);
    }

    public void addOutPort(final OutPort _outPort) {
        outPorts.add(_outPort);
    }

    public void getNext(final InPort _inPort) {
        activity = _inPort;
    }

    abstract public void gotNext(final InPort _inport, final Object _e);

    public void write(final OutPort _outPort, final Object _e) {
        activity = _outPort;
        _outPort.write(_e);
    }

    abstract public void wrote(final OutPort _outPort);
}
