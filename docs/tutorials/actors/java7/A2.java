class A2 extends GuardActor implements Reply<Void> {
    private Reply<Void> externalReply;
    
    void begin(Reply<Void> _externalReply, B2 _b) {
        lock(false);
        externalReply = _externalReply;
        unlock(true);
        _b.add1(this);
    }
    
    @Override
    public void response(Void value) {
        lock(true);
        System.out.println("added 1");
        unlock(false);
        externalReply.response(null);
    }
}
