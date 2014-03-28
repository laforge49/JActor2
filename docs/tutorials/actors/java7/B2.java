class B2 {
    private int count;
    
    synchronized void add1(Add1Reply _caller) {
        count += 1;
        _caller.reply();
    }
}
