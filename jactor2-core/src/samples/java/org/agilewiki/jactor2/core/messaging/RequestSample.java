package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.BladeBase;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class RequestSample {

    public static void main(String[] args) throws Exception {

        //A facility with two threads.
        final Facility facility = new Facility(2);

        try {

            //Create blade.
            SampleBlade2 bladeA = new SampleBlade2(new NonBlockingReactor(facility));

            //Initialize blade to 1.
            bladeA.updateAReq(1).signal();

            //Change blade to 2.
            System.out.println("was " + bladeA.updateAReq(2).call() + " but is now 2");

            //Create bladeB with a reference to blade.
            IndirectBlade bladeB = new IndirectBlade(bladeA, new NonBlockingReactor(facility));

            //Indirectly change blade to 42.
            System.out.println("was " + bladeB.indirectAReq(42).call() + " but is now 42");

        } finally {
            //shutdown the facility
            facility.close();
        }

    }

}

//A simple blade with state.
class SampleBlade2 extends BladeBase {

    //Initial state is 0.
    private int state = 0;

    //Create a SimpleBlade2.
    SampleBlade2(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Return an update request.
    AsyncRequest<Integer> updateAReq(final int _newState) {
        return new AsyncRequest<Integer>(getReactor()) {

            @Override
            public void processAsyncRequest() throws Exception {
                int oldState = state;
                state = _newState; //assign the new state
                processAsyncResponse(oldState); //return the old state.
            }
        };
    }

}

//A blade which operates on another blade.
class IndirectBlade extends BladeBase {

    //The other blade.
    private final SampleBlade2 blade;

    //Create an IndirectBlade with a reference to another blade.
    IndirectBlade(final SampleBlade2 _bladeA, final Reactor _reactor) throws Exception {
        blade = _bladeA;
        initialize(_reactor);
    }

    //Return a request to update the other blade and return its new state.
    AsyncRequest<Integer> indirectAReq(final int _newState) {
        return new AsyncRequest<Integer>(getReactor()) {
            AsyncRequest<Integer> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {

                //Get a request from the other blade.
                AsyncRequest<Integer> req = blade.updateAReq(_newState);

                //Send the request to the other blade.
                req.send(getMessageProcessor(), new AsyncResponseProcessor<Integer>() {

                    @Override
                    public void processAsyncResponse(Integer response) throws Exception {

                        //Return the old state.
                        dis.processAsyncResponse(response);
                    }
                });
            }
        };
    }
}