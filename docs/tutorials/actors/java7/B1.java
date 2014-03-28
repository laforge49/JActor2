class B1 {
    private int count;
    
    void add1(Add1Reply _caller) {
        synchronized(this) {
            count += 1;
        }
        _caller.reply();
    }
}
