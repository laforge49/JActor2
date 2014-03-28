class A implements Add1Reply {
    private StartReply caller;
    
    void start(StartReply _caller) {
        caller = _caller;
        (new B()).add1(this);
    }
    
    public void reply() {
        System.out.println("added 1");
        caller.reply();
    }
}
