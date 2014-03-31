class A implements Reply<Void> {
    private Reply<Void> externalReply;
    
    void begin(Reply<Void> _externalReply, B _b) {
        externalReply = _externalReply;
        _b.add1(this);
    }
    
    @Override
    public void response(Void value) {
        System.out.println("added 1");
        externalReply.response(null);
    }
}
