public class BankAccount2 {
    private int balance;
    private int hold;
    private boolean closed;

    public void deposit(int amount, Reply<Boolean> depositCompletion) {
        boolean depositValid;
        synchronized(this) {
            depositValid = !closed;
            if (depositValid)
                balance += amount;
        }
        depositCompletion.response(depositValid);
    }

    public void transfer(final int amount, final BankAccount2 toAccount, final Reply<Boolean> transferCompletion) {
            boolean transferValid = false;
            synchronized(this) {
                if (amount <= balance) {
                    balance -= amount;
                    hold += amount;
                    transferValid = true;
                }
            }
            if (transferValid)
                toAccount.deposit(amount, (Boolean depositSuccessful) -> {
                    synchronized(BankAccount2.this) {
                        hold -= amount;
                        if (!depositSuccessful)
                            balance += amount;
                    }
                    transferCompletion.response(depositSuccessful);
                });
            else
                transferCompletion.response(false);
    }

    public static void main(String[] args) throws Exception {
        BankAccount2 accountA = new BankAccount2();
        BankAccount2 accountB = new BankAccount2();
        
        accountA.deposit(1000, (Boolean depositSuccessful) -> {
            if (depositSuccessful)
                System.out.println("deposit successful");
            else
                System.out.println("deposit failed");
            accountA.transfer(500, accountB, (Boolean transferSuccessful) -> {
                if (transferSuccessful)
                    System.out.println("transfer successful");
                else
                    System.out.println("transfer failed");
            });
        });
    }
}
