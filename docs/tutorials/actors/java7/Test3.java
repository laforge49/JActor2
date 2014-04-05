public class Test3 implements Source {
    BankAccount3 accountA = new BankAccount3();
    BankAccount3 accountB = new BankAccount3();

    public static void main(String[] args) {
        Test3 test = new Test3();
        test.begin();
    }

    void begin() {
         accountA.deposit(1000, this);
    }

    @Override
    public void depositCompletion(boolean depositSuccessful) {
        if (depositSuccessful)
            System.out.println("deposit successful");
        else
            System.out.println("deposit failed");
        accountA.transfer(500, accountB, this);
    }
    
    @Override
    public void transferCompletion(boolean transferSuccessful) {
        if (transferSuccessful)
            System.out.println("transfer successful");
        else
            System.out.println("transfer failed");
    }
}
