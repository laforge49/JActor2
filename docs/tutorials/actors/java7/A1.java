class A1 extends BasicActor {
    private Caller caller;
    
    void start(Caller _caller, B1 _b) {
        start();
        caller = _caller;
        finish();
        _b.add1(this);
    }
    
    public void reply() {
        start();
        System.out.println("added 1");
        finish();
        caller.reply();
    }
}
