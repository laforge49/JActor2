public class BankAccount {
    private int balance;
    private int hold;
    private boolean closed;

    public boolean deposit(int amount) {
        if (closed)
            return false;
        balance += amount;
        return true;
    }

    public boolean transfer(int amount, BankAccount toAccount) {
        if (amount > balance)
            return false;
        balance -= amount;
        hold += amount;
        boolean depositSuccessful = toAccount.deposit(amount);
        hold -= amount;
        if (!depositSuccessful)
            balance += amount;
        return depositSuccessful;
    }
    
    public void close() {
        closed = true;
    }

    public static void main(String[] args) throws Exception {
        BankAccount accountA = new BankAccount();
        BankAccount accountB = new BankAccount();
        
        boolean depositSuccessful = accountA.deposit(1000);
        if (depositSuccessful)
            System.out.println("deposit successful");
        else
            System.out.println("deposit failed");

        boolean transferSuccessful = accountA.transfer(500, accountB);
        if (transferSuccessful)
            System.out.println("transfer successful");
        else
            System.out.println("transfer failed");
    }
}
