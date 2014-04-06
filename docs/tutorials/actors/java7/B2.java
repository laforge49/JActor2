class B2 extends GuardActor {
    private int count;
    
    void add1(Reply<Void> _reply) {
        lock(false);
        count += 1;
        unlock(false);
        _reply.response(null);
    }
}
