class B {
    private int count;
    
    void add1(Add1Reply _caller) {
        count += 1;
        _caller.reply();
    }
}
