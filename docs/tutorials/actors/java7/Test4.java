public class Test4 implements Source {
    BankAccount4 accountA = new BankAccount4();
    BankAccount4 accountB = new BankAccount4();

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
