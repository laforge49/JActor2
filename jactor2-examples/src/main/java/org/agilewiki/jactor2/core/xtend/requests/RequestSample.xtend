package org.agilewiki.jactor2.core.xtend.requests;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

class RequestSample {

    def static void main(String[] args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create blades.
            val bladeA = new SampleBlade2(new NonBlockingReactor());

            //Initialize blades to 1.
            bladeA.updateAReq(1).signal();

            //Change blades to 2.
            System.out.println("was " + bladeA.updateAReq(2).call()
                    + " but is now 2");

            //Create bladeB with a reference to blades.
            val bladeB = new IndirectBlade(bladeA,
                    new NonBlockingReactor());

            //Indirectly change blades to 42.
            System.out.println("was " + bladeB.indirectAReq(42).call()
                    + " but is now 42");

        } finally {
            //shutdown the facility
            Plant.close();
        }

    }

}

//A simple blades with state.
class SampleBlade2 extends NonBlockingBladeBase {

    //Initial state is 0.
    var int state = 0;

    //Create a SimpleBlade2.
    new(NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    //Return an update request.
    def AsyncRequest<Integer> updateAReq(int _newState) {
        return new AsyncRequest<Integer>(this) {
            override void processAsyncRequest() {
                val oldState = state;
                state = _newState; //assign the new state
                processAsyncResponse(oldState); //return the old state.
            }
        };
    }

}

//A blades which operates on another blades.
class IndirectBlade extends NonBlockingBladeBase {

    //The other blades.
    val SampleBlade2 blade;

    //Create an IndirectBlade with a reference to another blades.
    new(SampleBlade2 _bladeA, NonBlockingReactor _reactor)
            throws Exception {
        super(_reactor);
        blade = _bladeA;
    }

    //Return a request to update the other blades and return its new state.
    def AsyncRequest<Integer> indirectAReq(int _newState) {
        return new AsyncRequest<Integer>(this) {
            val dis = this;
            override void processAsyncRequest() {

                //Get a request from the other blades.
                val req = blade.updateAReq(_newState);

                //Send the request to the other blades.
                send(req, new AsyncResponseProcessor<Integer>() {

                    override void processAsyncResponse(Integer response) {

                        //Return the old state.
                        dis.processAsyncResponse(response);
                    }
                });
            }
        };
    }
}