class A2 implements Add1Reply {
    private StartReply caller;
    
    synchronized void start(StartReply _caller) {
        caller = _caller;
        (new B2()).add1(this);
    }
    
    synchronized public void reply() {
        System.out.println("added 1");
        caller.reply();
    }
}
