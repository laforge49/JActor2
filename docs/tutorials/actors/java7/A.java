class A {
    private Caller caller;
    
    void start(Caller _caller, B _b) {
        caller = _caller;
        _b.add1(this);
    }
    
    public void reply() {
        System.out.println("added 1");
        caller.reply();
    }
}
