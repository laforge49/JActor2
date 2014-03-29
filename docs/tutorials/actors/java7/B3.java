class B3 extends BasicActor {
    private int count;
    
    void add1(final Reply<Void> _reply) {
        (new Runnable() {
            @Override
            public void run() {
                start();
                count += 1;
                finish();
                _reply.response(null);
            }
        }).run();
    }
}