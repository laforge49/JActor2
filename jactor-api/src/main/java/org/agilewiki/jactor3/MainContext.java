package org.agilewiki.jactor3;

import java.util.concurrent.Semaphore;

public class MainContext extends MessageImpl<Actor> {

    public MainContext(Actor _targetActor) {
        super(_targetActor);
    }

    @Override
    public Message iteration() {
        throw new UnsupportedOperationException();
    }
}
