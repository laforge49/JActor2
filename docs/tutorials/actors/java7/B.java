class B {
    private int count;
    
    void add1(Reply<Void> _reply) {
        count += 1;
        _reply.response(null);
    }
}
