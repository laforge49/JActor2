package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class EventSample {

    public static void main(String[] args) throws Exception {

        //A facility with one thread.
        final Facility facility = new Facility(1);

        //Create a SampleActor1 instance.
        SampleActor1 sampleActor1 = new SampleActor1(new NonBlockingReactor(facility));

        new FinEvent("finished").signal(sampleActor1);

        //Hang until exit.
        Thread.sleep(1000000);

    }
}

class SampleActor1 extends ActorBase {

    SampleActor1(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Print "finished" and exit when fin is called.
    void fin(final String msg) throws Exception {
        System.out.println(msg);
        System.exit(0);
    }
}

//When a FinEvent is passed to an actor, the fin method is called.
class FinEvent extends Event<SampleActor1> {
    private final String msg;

    FinEvent(final String _msg) {
        msg = _msg;
    }

    @Override
    public void processEvent(SampleActor1 _targetActor) throws Exception {
        _targetActor.fin(msg);
    }
}
