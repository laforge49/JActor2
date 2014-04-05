public class BankAccount2 {
    private int balance;
    private int hold;
    private boolean closed;

    public void deposit(int amount, Reply<Boolean> depositCompletion) {
        boolean success;
        synchronized(this) {
            success = !closed;
            if (success)
                balance += amount;
        }
        depositCompletion.response(success);
    }

    public void transfer(final int amount, final BankAccount2 toAccount, final Reply<Boolean> transferCompletion) {
            Reply<Boolean> onDeposit = new Reply<Boolean>() {
                @Override
                public void response(Boolean depositSuccessful) {
                    synchronized(BankAccount2.this) {
                        hold -= amount;
                    if (!depositSuccessful)
                        balance += amount;
                    }
                    transferCompletion.response(depositSuccessful);
                }
            };

            boolean success = false;
            synchronized(this) {
                if (amount <= balance) {
                    balance -= amount;
                    hold += amount;
                    success = true;
                }
            }
            if (success)
                toAccount.deposit(amount, onDeposit);
            else
                transferCompletion.response(false);
    }

    public static void main(String[] args) throws Exception {
        BankAccount2 accountA = new BankAccount2();
        BankAccount2 accountB = new BankAccount2();
        
        Reply<Boolean> onDeposit = new Reply<Boolean>() {
            @Override
            public void response(Boolean depositSuccessful) {
                Reply<Boolean> onTransfer = new Reply<Boolean>() {
                    @Override
                    public void response(Boolean transferSuccessful) {
                        if (transferSuccessful)
                            System.out.println("transfer successful");
                        else
                            System.out.println("transfer failed");
                    }
                };
                
                if (depositSuccessful)
                    System.out.println("deposit successful");
                else
                    System.out.println("deposit failed");
                accountA.transfer(500, accountB, onTransfer);
            };
        };
        
        accountA.deposit(1000, onDeposit);
    }
}
