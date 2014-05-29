import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class BankAccount extends NonBlockingBladeBase {
    private int balance;
    private int hold;
    
    public BankAccount() throws Exception {}

    public SyncRequest<Void> depositSReq(final int _amount) {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() {
                balance += _amount;
                return null;
            }
        };
    }

    public AsyncRequest<Boolean> transferAReq(final int _amount, final BankAccount _account) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncRequest<Boolean> dis = this;

            ExceptionHandler<Boolean> depositExceptionHandler = new ExceptionHandler<Boolean>() {
                @Override
                public Boolean processException(Exception e) {
                    hold -= _amount;
                    balance += _amount;
                    return false;
                }
            };

            AsyncResponseProcessor<Void> depositResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) {
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
            BankAccount account1 = new BankAccount();
            account1.depositSReq(1000).call();
            BankAccount account2 = new BankAccount();
            System.out.println(account1.transferAReq(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
