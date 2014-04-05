public class BankAccount3 implements Source {
    private int balance;
    private int hold;
    private boolean closed;
    
    private Source transferSource;
    private int transferAmount;

    public void deposit(int amount, Source source) {
        boolean depositValid;
        synchronized(this) {
            depositValid = !closed;
            if (depositValid)
                balance += amount;
        }
        source.depositCompletion(depositValid);
    }

    public void transfer(int amount, BankAccount3 toAccount, Source source) {
            boolean transferValid = false;
            synchronized(this) {
                if (amount <= balance) {
                    balance -= amount;
                    hold += amount;
                    transferValid = true;
                }
            }
            if (transferValid) {
                transferAmount = amount;
                transferSource = source;
                toAccount.deposit(amount, this);
            }
            else
                source.transferCompletion(false);
    }
    
    @Override
    public void depositCompletion(boolean depositSuccessful) {
        synchronized(this) {
            hold -= transferAmount;
            if (!depositSuccessful)
                balance += transferAmount;
        }
        transferSource.transferCompletion(depositSuccessful);
    }
    
    @Override
    public void transferCompletion(boolean success) {
        throw new UnsupportedOperationException();
    }
}
