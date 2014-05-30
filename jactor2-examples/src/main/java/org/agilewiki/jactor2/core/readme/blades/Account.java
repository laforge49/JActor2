package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class Account extends NonBlockingBladeBase {
    private int balance;
    private int hold;

    public Account() throws Exception {
    }

    private void deposit(final int _amount) {
        balance += _amount;
    }

    public void deposit(final int _amount, final Reactor sourceReactor) {
        directCheck(sourceReactor);
        _deposit(_amount);
    }

    public SyncRequest<Void> depositSReq(final int _amount) {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                _deposit(_amount);
                return null;
            }
        };
    }

    public AsyncRequest<Boolean> transferAReq(final int _amount,
            final Account _account) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncRequest<Boolean> dis = this;

            ExceptionHandler<Boolean> depositExceptionHandler = new ExceptionHandler<Boolean>() {
                @Override
                public Boolean processException(final Exception e)
                        throws Exception {
                    hold -= _amount;
                    balance += _amount;
                    return false;
                }
            };

            AsyncResponseProcessor<Void> depositResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) {
                    hold -= _amount;
                    dis.processAsyncResponse(true);
                }
            };

            @Override
            public void processAsyncRequest() {
                if (_amount > balance)
                    dis.processAsyncResponse(false);
                balance -= _amount;
                hold += _amount;
                setExceptionHandler(depositExceptionHandler);
                send(_account.depositSReq(_amount), depositResponseProcessor);
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final Account account1 = new Account();
            account1.depositSReq(1000).call();
            final Account account2 = new Account();
            System.out.println(account1.transferAReq(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
