class A3 {
    void start(final Reply<Void> _reply) {
        Reply<Void> add1Reply = new Reply<Void>() {
            @Override
            public void response(Void value) {
                synchronized(A3.this) {
                    System.out.println("added 1");
                }
                _reply.response(null);
            }
        };
        
        (new Runnable() {
            @Override
            public void run() {
                synchronized(A3.this) {
                }
                (new B3()).add1(add1Reply);
            }
        }).run();
    }
}