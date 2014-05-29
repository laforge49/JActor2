package org.agilewiki.jactor2.core.xtend.blades

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase
import org.agilewiki.jactor2.core.impl.Plant
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor
import org.agilewiki.jactor2.core.requests.ExceptionHandler
import org.agilewiki.jactor2.core.requests.SyncRequest

class Account extends NonBlockingBladeBase {
    var int balance;
    var int hold;

    new() throws Exception {
    }

    def SyncRequest<Void> depositSReq(int _amount) {
        return new SyncRequest<Void>(this) {
            override Void processSyncRequest() {
                balance = balance + _amount;
                return null;
            }
        }
    }

    def AsyncRequest<Boolean> transferAReq(int _amount, Account _account) {
        return new AsyncRequest<Boolean>(this) {
            val dis = this;

            val depositExceptionHandler = new ExceptionHandler<Boolean>() {
                override Boolean processException(Exception e) {
                    hold = hold - _amount;
                    balance = balance + _amount;
                    return false;
                }
            };

            val depositResponseProcessor = new AsyncResponseProcessor<Void>() {
                override void processAsyncResponse(Void _response) {
                    hold = hold - _amount;
                    dis.processAsyncResponse(true);
                }
            };

            override void processAsyncRequest() {
                if (_amount > balance)
                    dis.processAsyncResponse(false);
                balance = balance - _amount;
                hold = hold + _amount;
                setExceptionHandler(depositExceptionHandler);

                send(_account.depositSReq(_amount), depositResponseProcessor);
            }
        };
    }

    def static void main(String[] _args) {
        new Plant();
        try {
            val account1 = new Account();
            account1.depositSReq(1000).call();
            val account2 = new Account();
            System.out.println(account1.transferAReq(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
