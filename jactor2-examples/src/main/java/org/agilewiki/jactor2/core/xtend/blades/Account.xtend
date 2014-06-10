package org.agilewiki.jactor2.core.xtend.blades

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase
import org.agilewiki.jactor2.core.impl.Plant
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.requests.ExceptionHandler
import org.agilewiki.jactor2.core.xtend.codegen.SReq

class Account extends NonBlockingBladeBase {
    var int balance;
    var int hold;

    new() throws Exception {
    }

	@SReq
    private def void _deposit(int _amount) {
        balance = balance + _amount;
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

            override void processAsyncRequest() {
                if (_amount > balance)
                    dis.processAsyncResponse(false);
                balance = balance - _amount;
                hold = hold + _amount;
                setExceptionHandler(depositExceptionHandler);
                send(_account.depositSReq(_amount),
                	[hold = hold - _amount; dis.processAsyncResponse(true)]);
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
