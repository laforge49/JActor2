package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class EventSample {

    public static void main(String[] args) throws Exception {

        //A facility with one thread.
        final Plant plant = new Plant(1);

        //Create a SampleBlade1 instance.
        SampleBlade1 sampleBlade1 = new SampleBlade1(new NonBlockingReactor(plant));

        new FinEvent("finished").signal(sampleBlade1);

        //Hang until exit.
        Thread.sleep(1000000);

    }
}

class SampleBlade1 extends BladeBase {

    SampleBlade1(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Print "finished" and exit when fin is called.
    void fin(final String msg) throws Exception {
        System.out.println(msg);
        System.exit(0);
    }
}

//When a FinEvent is passed to a blade, the fin method is called.
class FinEvent extends Event<SampleBlade1> {
    private final String msg;

    FinEvent(final String _msg) {
        msg = _msg;
    }

    @Override
    protected void processEvent(SampleBlade1 _targetBlade) throws Exception {
        _targetBlade.fin(msg);
    }
}
