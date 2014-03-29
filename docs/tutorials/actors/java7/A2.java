class A2 extends GuardActor implements Add1Reply {
    private StartReply caller;
    
    void start(StartReply _caller) {
        start(false);
        caller = _caller;
        finish(true);
        (new B2()).add1(this);
    }
    
    public void reply() {
        start(true);
        System.out.println("added 1");
        finish(false);
        caller.reply();
    }
}
