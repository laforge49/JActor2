class LoopTest {
    public static void main(String[] args) {
        final long count = 10000000;
        final long t0 = System.currentTimeMillis();
        
        Reply<String> reply = new Reply<String>() {
            public void response(String value) {
                long t1 = System.currentTimeMillis();
                long duration = t1 - t0;
                System.out.println("durration in millis: "+duration);
                System.out.println("loops per second: "+(count*1000/duration));
            }
        };
        
        new Looper<String>() {
            long ndx = count;
            
            public void iterate(Reply<String> reply) {
                if (ndx == 0) {
                    reply.response("hi!");
                    return;
                }
                ndx--;
                reply.response(null);
            }
        }.go(reply);
    }
}