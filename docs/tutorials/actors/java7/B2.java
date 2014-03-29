class B2 extends GuardActor {
    private int count;
    
    void add1(A2 _caller) {
        start(false);
        count += 1;
        finish(false);
        _caller.reply();
    }
}
