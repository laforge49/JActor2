public class BankAccount1 {
    private int balance;
    private int hold;
    private boolean closed;

    public void deposit(int amount, Reply<Boolean> depositCompletion) {
        if (closed) {
            depositCompletion.response(false);
            return;
        }
        balance += amount;
        depositCompletion.response(true);
    }

    public void transfer(final int amount, final BankAccount1 toAccount, final Reply<Boolean> transferCompletion) {
            if (amount > balance)
                transferCompletion.response(false);
            else {
                balance -= amount;
                hold += amount;
                toAccount.deposit(amount, (Boolean depositSuccessful) -> {
                    hold -= amount;
                    if (!depositSuccessful)
                        balance += amount;
                    transferCompletion.response(depositSuccessful);
                });
            }
    }

    public static void main(String[] args) throws Exception {
        BankAccount1 accountA = new BankAccount1();
        BankAccount1 accountB = new BankAccount1();

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
