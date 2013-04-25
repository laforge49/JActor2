package org.agilewiki.general;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class ServiceTest extends TestCase {
    public void test1() throws Exception {
        final MailboxFactory mailboxFactoryA = new DefaultMailboxFactoryImpl();
        try {
            final MailboxFactory mailboxFactoryB = new DefaultMailboxFactoryImpl();
            ActorA actorA;
            ActorB actorB;
            try {
                Mailbox mailboxB = mailboxFactoryB.createMailbox();
                actorB = new ActorB(mailboxB);
                assertEquals(42, (int) actorB.getReq().call());
                Mailbox mailboxA = mailboxFactoryA.createMailbox();
                actorA = new ActorA(mailboxA, actorB);
                actorA.startReq().signal();
            } finally {
                mailboxFactoryB.close();
            }
            assertEquals(true, (boolean) actorA.doneReq().call());
            boolean ex = false;
            try {
                actorB.getReq().call();
            } catch (ServiceClosedException x) {
                ex = true;
            }
            assertEquals(true, ex);
        } finally {
            mailboxFactoryA.close();
        }
    }
}

class ActorA extends ActorBase {
    final ActorB actorB;
    ResponseProcessor<Boolean> doneRP;
    boolean gotIt;

    ActorA(final Mailbox _mailbox, final ActorB _actorB) throws Exception {
        initialize(_mailbox);
        actorB = _actorB;
    }

    Request<Void> startReq() {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _rp) throws Exception {
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        if (!(throwable instanceof ServiceClosedException))
                            throw throwable;
                        gotIt = true;
                        _rp.processResponse(null);
                        doneRP.processResponse(gotIt);
                    }
                });
                actorB.slowReq().send(getMailbox(), new ResponseProcessor<Integer>() {
                    @Override
                    public void processResponse(Integer response) throws Exception {
                        doneRP.processResponse(gotIt);
                    }
                });
            }
        };
    }

    Request<Boolean> doneReq() {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(Transport<Boolean> _rp) throws Exception {
                doneRP = _rp;
            }
        };
    }
}

class ActorB extends ActorBase {
    ActorB(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    Request<Integer> getReq() {
        return new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport<Integer> _rp) throws Exception {
                _rp.processResponse(42);
            }
        };
    }

    Request<Integer> slowReq() {
        return new RequestBase<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport<Integer> _rp) throws Exception {
                long l = 0;
                while (l < 1000000000)
                    l += 1;
                _rp.processResponse(42);
            }
        };
    }
}
