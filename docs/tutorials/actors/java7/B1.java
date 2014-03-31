class B1 {
    private int count;
    
    void add1(Reply<Void> _reply) {
        synchronized(this) {
            count += 1;
        }
        _reply.response(null);
    }
}
