package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.messaging.Event;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Transport;

public class JAIteratorTest extends TestCase {
    private Mailbox mailbox;
    private Mailbox counterMailbox;
    private long runs;

    /*
shared mailbox test
Number of runs: 100000000
Count: 100000000
Test time in milliseconds: 4126
Messages per second: 24236548
----------------------------------------
commandeering mailbox test
Number of runs: 100000000
Count: 100000000
Test time in milliseconds: 7246
Messages per second: 13800717
----------------------------------------
migration mailbox test
Number of runs: 100000000
Count: 100000000
Test time in milliseconds: 7291
Messages per second: 13715539
     */
    public void test1() throws Exception {
        System.gc();
        runs = 10;
        System.out.println("shared mailbox test");
        DefaultMailboxFactory mailboxFactory = new DefaultMailboxFactory();
        try {
            mailbox = mailboxFactory.createNonBlockingMailbox();
            counterMailbox = mailbox;
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    public void test2() throws Exception {
        System.gc();
        runs = 10;
        System.out.println("commandeering mailbox test");
        DefaultMailboxFactory mailboxFactory = new DefaultMailboxFactory();
        try {
            mailbox = mailboxFactory.createNonBlockingMailbox();
            counterMailbox = mailboxFactory.createNonBlockingMailbox();
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    public void test3() throws Exception {
        System.gc();
        runs = 10;
        System.out.println("migration mailbox test");
        DefaultMailboxFactory mailboxFactory = new DefaultMailboxFactory();
        try {
            mailbox = mailboxFactory.createNonBlockingMailbox();
            counterMailbox = mailboxFactory.createNonBlockingMailbox();
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Void> runReq() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                final CounterActor counterActor = new CounterActor(counterMailbox);
                final Request urr = counterActor.resetReq;
                final AddEvent uar = new AddEvent(1);
                JAIterator pait = new JAIterator() {
                    long i = 0;

                    @Override
                    protected void process(ResponseProcessor rp1)
                            throws Exception {
                        if (i == runs)
                            rp1.processResponse(this);
                        else {
                            i += 1;
                            uar.signal(counterActor);
                            rp1.processResponse(null);
                        }
                    }
                };
                final long start = System.currentTimeMillis();
                pait.iterate(new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response)
                            throws Exception {
                        urr.send(mailbox,
                                new ResponseProcessor<Long>() {
                                    @Override
                                    public void processResponse(final Long count)
                                            throws Exception {
                                        long finish = System
                                                .currentTimeMillis();
                                        long elapsedTime = finish - start;
                                        System.out.println("Number of runs: "
                                                + runs);
                                        System.out.println("Count: " + count);
                                        System.out
                                                .println("Test time in milliseconds: "
                                                        + elapsedTime);
                                        if (elapsedTime > 0)
                                            System.out
                                                    .println("Messages per second: "
                                                            + (runs * 1000 / elapsedTime));
                                        _rp.processResponse(null);
                                    }
                                });
                    }
                });
            }
        };
    }
}

class AddEvent extends Event<CounterActor> {
    private final long inc;

    AddEvent(final long _inc) {
        inc = _inc;
    }

    @Override
    public void processEvent(final CounterActor _targetActor) throws Exception {
        _targetActor.add(inc);
    }
}

class CounterActor extends ActorBase {
    private long count = 0L;

    public Request<Long> resetReq;

    CounterActor(Mailbox mailbox) throws Exception {
        initialize(mailbox);
        resetReq = new Request<Long>(getMailbox()) {
            @Override
            public void processRequest(Transport<Long> _transport) throws Exception {
                _transport.processResponse(reset());
            }
        };
    }

    public void add(long inc) {
        count += inc;
    }

    public long reset() throws Exception {
        long rv = count;
        count = 0;
        return rv;
    }
}
