class B3 {
    private int count;
    
    void add1(final Reply<Void> _reply) {
        (new Runnable() {
            @Override
            public void run() {
                synchronized(B3.this) {
                    count += 1;
                }
                _reply.response(null);
            }
        }).run();
    }
}