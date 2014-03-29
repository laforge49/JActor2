class A2 extends GuardActor {
    private Caller caller;
    
    void start(Caller _caller, B2 _b) {
        start(false);
        caller = _caller;
        finish(true);
        _b.add1(this);
    }
    
    public void reply() {
        start(true);
        System.out.println("added 1");
        finish(false);
        caller.reply();
    }
}
