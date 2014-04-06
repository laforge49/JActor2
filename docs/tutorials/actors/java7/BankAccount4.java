public class BankAccount4 extends GuardActor implements Source {
    private int balance;
    private int hold;
    private boolean closed;
    
    private Source transferSource;
    private int transferAmount;

    public void deposit(int amount, Source source) {
        boolean depositValid;
        lock(false);
        depositValid = !closed;
        if (depositValid)
            balance += amount;
        unlock(false);
        source.depositCompletion(depositValid);
    }

    public void transfer(int amount, BankAccount4 toAccount, Source source) {
        boolean transferValid = false;
        lock(false);
        if (amount <= balance) {
            balance -= amount;
            hold += amount;
            transferValid = true;
        }
        if (transferValid) {
            transferAmount = amount;
            transferSource = source;
            unlock(true);
            toAccount.deposit(amount, this);
        } else {
            unlock(false);
            source.transferCompletion(false);
        }
    }
    
    @Override
    public void depositCompletion(boolean depositSuccessful) {
        lock(true);
        hold -= transferAmount;
        if (!depositSuccessful)
            balance += transferAmount;
        unlock(false);
        transferSource.transferCompletion(depositSuccessful);
    }
    
    @Override
    public void transferCompletion(boolean success) {
        throw new UnsupportedOperationException();
    }
}
