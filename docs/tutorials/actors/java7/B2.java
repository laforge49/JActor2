class B2 extends GuardActor {
    private int count;
    
    void add1(Reply<Void> _reply) {
        start(false);
        count += 1;
        finish(false);
        _reply.response(null);
    }
}
