import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class Account extends NonBlockingBladeBase {
    private int balance;
    private int hold;

    public Account() throws Exception {
    }

    public SyncRequest<Void> depositSReq(final int _amount) {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                balance += _amount;
                return null;
            }
        };
    }

    public AsyncRequest<Boolean> transferAReq(final int _amount, final Account _account) {
        return new AsyncBladeRequest<Boolean>() {
            AsyncRequest<Boolean> dis = this;

            ExceptionHandler<Boolean> depositExceptionHandler = new ExceptionHandler<Boolean>() {
                @Override
                public Boolean processException(Exception e) throws Exception {
                    return false;
                }
            };

            AsyncResponseProcessor<Void> depositResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    hold -= _amount;
                    dis.processAsyncResponse(true);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                if (_amount > balance)
                    dis.processAsyncResponse(false);
                setExceptionHandler(depositExceptionHandler);
                send(_account.depositSReq(_amount), depositResponseProcessor);
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Account account1 = new Account();
            account1.depositSReq(1000).call();
            Account account2 = new Account();
            System.out.println(account1.transferAReq(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
