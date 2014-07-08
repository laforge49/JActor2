package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.*;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

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
        deposit(_amount);
    }

    public SOp<Void> depositSOp(final int _amount) {
        return new SOp<Void>("deposit", getReactor()) {
            @Override
            public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                deposit(_amount);
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
                send(_account.depositSOp(_amount), depositResponseProcessor);
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final Account account1 = new Account();
            account1.depositSOp(1000).call();
            final Account account2 = new Account();
            System.out.println(account1.transferAReq(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
