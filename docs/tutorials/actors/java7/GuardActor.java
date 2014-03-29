class GuardActor {
    private volatile boolean busy;
    private volatile boolean replyExpected;
    
    protected void start(boolean isReply) {
        if (!replyExpected && isReply)
            throw new UnsupportedOperationException("Reply received when none expected");
        while (busy || !replyExpected || isReply)
            Thread.yield();
        busy = true;
    }
    
    protected void finish(boolean expectingReply) {
        replyExpected = expectingReply;
        busy = false;
    }
}
