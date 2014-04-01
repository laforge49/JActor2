class A2 extends GuardActor implements Reply<Void> {
    private Reply<Void> externalReply;
    
    void begin(Reply<Void> _externalReply, B2 _b) {
        start(false);
        externalReply = _externalReply;
        finish(true);
        _b.add1(this);
    }
    
    @Override
    public void response(Void value) {
        start(true);
        System.out.println("added 1");
        finish(false);
        externalReply.response(null);
    }
}
