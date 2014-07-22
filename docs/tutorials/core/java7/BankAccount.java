import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class BankAccount extends NonBlockingBladeBase {
    private int balance;
    private int hold;
    
    public BankAccount() throws Exception {}

    public SOp<Void> depositSOp(final int _amount) {
        return new SOp<Void>("deposit", getReactor()) {
            @Override
            public Void processSyncOperation(final RequestImpl _requestImpl) {
                balance += _amount;
                return null;
            }
        };
    }

    public AOp<Boolean> transferAOp(final int _amount, final BankAccount _account) {
        return new AOp<Boolean>("transfer", getReactor()) {
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Boolean> _asyncResponseProcessor) throws Exception {
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
					public void processAsyncResponse(Void _response) throws Exception {
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
            BankAccount account1 = new BankAccount();
            account1.depositSOp(1000).call();
            BankAccount account2 = new BankAccount();
            System.out.println(account1.transferAOp(500, account2).call());
        } finally {
            Plant.close();
        }
    }
}
