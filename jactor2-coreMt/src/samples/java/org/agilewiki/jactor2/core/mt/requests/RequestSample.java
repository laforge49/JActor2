package org.agilewiki.jactor2.core.mt.requests;

import org.agilewiki.jactor2.core.Plant;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class RequestSample {

    public static void main(String[] args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create blades.
            SampleBlade2 bladeA = new SampleBlade2(new NonBlockingReactor());

            //Initialize blades to 1.
            bladeA.updateAReq(1).signal();

            //Change blades to 2.
            System.out.println("was " + bladeA.updateAReq(2).call() + " but is now 2");

            //Create bladeB with a reference to blades.
            IndirectBlade bladeB = new IndirectBlade(bladeA, new NonBlockingReactor());

            //Indirectly change blades to 42.
            System.out.println("was " + bladeB.indirectAReq(42).call() + " but is now 42");

        } finally {
            //shutdown the facility
            Plant.close();
        }

    }

}

//A simple blades with state.
class SampleBlade2 extends NonBlockingBladeBase {

    //Initial state is 0.
    private int state = 0;

    //Create a SimpleBlade2.
    SampleBlade2(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    //Return an update request.
    AsyncRequest<Integer> updateAReq(final int _newState) {
        return new AsyncBladeRequest<Integer>() {

            @Override
            public void processAsyncRequest() {
                int oldState = state;
                state = _newState; //assign the new state
                processAsyncResponse(oldState); //return the old state.
            }
        };
    }

}

//A blades which operates on another blades.
class IndirectBlade extends NonBlockingBladeBase {

    //The other blades.
    private final SampleBlade2 blade;

    //Create an IndirectBlade with a reference to another blades.
    IndirectBlade(final SampleBlade2 _bladeA, final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
        blade = _bladeA;
    }

    //Return a request to update the other blades and return its new state.
    AsyncRequest<Integer> indirectAReq(final int _newState) {
        return new AsyncBladeRequest<Integer>() {
            AsyncRequest<Integer> dis = this;

            @Override
            public void processAsyncRequest() {

                //Get a request from the other blades.
                AsyncRequest<Integer> req = blade.updateAReq(_newState);

                //Send the request to the other blades.
                send(req, new AsyncResponseProcessor<Integer>() {

                    @Override
                    public void processAsyncResponse(Integer response) {

                        //Return the old state.
                        dis.processAsyncResponse(response);
                    }
                });
            }
        };
    }
}