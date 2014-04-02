class A3 {
    void begin(final Reply<Void> externalReply, final B3 _b) {
       (new Runnable() {
            Reply<Void> add1Reply = new Reply<Void>() {
                @Override
                public void response(Void value) {
                    System.out.println("added 1");
                    externalReply.response(null);
                }
            };
        
            @Override
            public void run() {
                _b.add1(add1Reply);
            }
        }).run();
    }
}
