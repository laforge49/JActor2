package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

public class MainContext extends MessageImpl<Actor> {

    MainContext(Actor _targetActor) {
        super(_targetActor);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }
}
