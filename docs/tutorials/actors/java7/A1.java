class A1 implements Add1Reply {
    private StartReply caller;
    
    void start(StartReply _caller) {
        synchronized(this) {
            caller = _caller;
        }
        (new B1()).add1(this);
    }
    
    public void reply() {
        synchronized(this) {
            System.out.println("added 1");
        }
        caller.reply();
    }
}
