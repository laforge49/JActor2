class BasicActor {
    private volatile boolean busy;
    
    protected void start() {
        while (busy)
            Thread.yield();
        busy = true;
    }
    
    protected void finish() {
        busy = false;
    }
}
