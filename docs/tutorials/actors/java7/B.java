class B {
    private int count;
    
    void add1(A _caller) {
        count += 1;
        _caller.reply();
    }
}
