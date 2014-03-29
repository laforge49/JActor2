class A1 extends BasicActor implements Add1Reply {
    private StartReply caller;
    
    void start(StartReply _caller) {
        start();
        caller = _caller;
        finish();
        (new B1()).add1(this);
    }
    
    public void reply() {
        start();
        System.out.println("added 1");
        finish();
        caller.reply();
    }
}
