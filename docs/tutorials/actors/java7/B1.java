class B1 extends BasicActor {
    private int count;
    
    void add1(Add1Reply _caller) {
        start();
        count += 1;
        finish();
        _caller.reply();
    }
}
