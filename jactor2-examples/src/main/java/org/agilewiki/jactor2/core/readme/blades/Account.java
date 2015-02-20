package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

public class Account extends NonBlockingBladeBase {
    private int balance;
    private int hold;

    public Account() throws Exception {
    }

    public SOp<Void> depositSOp(final int _amount) {
        return new SOp<Void>("deposit", getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                balance += _amount;
                return null;
            }
        };
    }

    public AOp<Boolean> transferAOp(final int _amount,
                                             final Account _account) {
        return new AOp<Boolean>("transfer", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Boolean> _asyncResponseProcessor)
                    throws Exception {
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
                    public void processAsyncResponse(final Void _response) throws Exception {
                        hold -= _amount;
                        _asyncResponseProcessor.processAsyncResponse(true);
                    }
                };

                if (_amount > balance)
                    _asyncResponseProcessor.processAsyncResponse(false);
                balance -= _amount;
                hold += _amount;
                _asyncRequestImpl.setExceptionHandler(depositExceptionHandler);
                _asyncRequestImpl.send(_account.depositSOp(_amount), depositResponseProcessor);
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            final Account account1 = new Account();
            account1.depositSOp(1000).call();
            final Account account2 = new Account();
            System.out.println(account1.transferAOp(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
