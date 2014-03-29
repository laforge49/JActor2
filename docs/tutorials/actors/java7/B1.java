class B1 extends BasicActor {
    private int count;
    
    void add1(A1 _caller) {
        start();
        count += 1;
        finish();
        _caller.reply();
    }
}
