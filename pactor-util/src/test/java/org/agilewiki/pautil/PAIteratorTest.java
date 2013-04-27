package org.agilewiki.pautil;

import junit.framework.TestCase;

import org.agilewiki.pactor.api.*;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PAIteratorTest extends TestCase {
    private Mailbox mailbox;
    private Mailbox counterMailbox;
    private long runs;

    public void test1() throws Exception {
        runs = 100;

//                shared mailbox test
//                Number of runs: 100,000,000
//                Count: 100,000,000
//                Test time in milliseconds: 4080
//                Messages per second: 24,509,803

        System.out.println("shared mailbox test");
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            mailbox = mailboxFactory.createMailbox();
            counterMailbox = mailbox;
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    public void test2() throws Exception {
        runs = 10;

//        async mailbox test
//        Number of runs: 1000000
//        Count: 1000000
//       Test time in milliseconds: 691
//        Messages per second: 1,447,178

        System.out.println("async mailbox test");
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            mailbox = mailboxFactory.createMailbox();
            counterMailbox = mailboxFactory.createMailbox(true);
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Void> runReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                final CounterActor counterActor = new CounterActor();
                counterActor.initialize(counterMailbox);
                final UnboundAddReq uar = new UnboundAddReq(1);
                final UnboundResetReq urr = new UnboundResetReq();
                PAIterator pait = new PAIterator() {
                    long i = 0;

                    @Override
                    protected void process(ResponseProcessor rp1)
                            throws Exception {
                        if (i == runs)
                            rp1.processResponse(this);
                        else {
                            i += 1;
                            uar.send(mailbox, counterActor, rp1);
                        }
                    }
                };
                final long start = System.currentTimeMillis();
                pait.iterate(new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response)
                            throws Exception {
                        urr.send(mailbox, counterActor,
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

class UnboundAddReq extends UnboundRequestBase<Void, CounterActor> {
    private final long inc;

    UnboundAddReq(final long _inc) {
        inc = _inc;
    }

    @Override
    public void processRequest(final CounterActor _targetActor,
            final Transport<Void> _rp) throws Exception {
        _targetActor.add(inc);
        _rp.processResponse(null);
    }
}

class UnboundResetReq extends UnboundRequestBase<Long, CounterActor> {

    @Override
    public void processRequest(final CounterActor _targetActor,
            final Transport<Long> _rp) throws Exception {
        _rp.processResponse(_targetActor.reset());
    }
}

class CounterActor extends ActorBase {
    private long count = 0L;

    public void add(long inc) {
        count += inc;
    }

    public long reset() throws Exception {
        long rv = count;
        count = 0;
        return rv;
    }
}
