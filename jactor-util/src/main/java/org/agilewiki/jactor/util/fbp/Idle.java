package org.agilewiki.jactor.util.fbp;

public class Idle implements Activity {

    @Override
    public void run() {
        try {
            wait();
        } catch (InterruptedException e) {
        }
    }
}
