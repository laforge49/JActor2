class A3 extends BasicActor {
    void start(final Reply<Void> _reply) {
        Reply<Void> add1Reply = new Reply<Void>() {
            @Override
            public void response(Void value) {
                start();
                System.out.println("added 1");
                finish();
                _reply.response(null);
            }
        };
        
        (new Runnable() {
            @Override
            public void run() {
                start();
                finish();
                (new B3()).add1(add1Reply);
            }
        }).run();
    }
}