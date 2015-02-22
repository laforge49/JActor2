package org.agilewiki.jactor2.core.readme.requests;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AIOp;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class RequestSample {

    public static void main(String[] args) throws Exception {

        //A facility with two threads.
        new Plant(2);

        try {

            //Create blades.
            SampleBlade2 bladeA = new SampleBlade2(new NonBlockingReactor());

            //Initialize blades to 1.
            bladeA.updateAOp(1).signal();

            //Change blades to 2.
            System.out.println("was " + bladeA.updateAOp(2).call() + " but is now 2");

            //Create bladeB with a reference to blades.
            IndirectBlade bladeB = new IndirectBlade(bladeA, new NonBlockingReactor());

            //Indirectly change blades to 42.
            System.out.println("was " + bladeB.indirectAOp(42).call() + " but is now 42");

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
    AIOp<Integer> updateAOp(final int _newState) {
        return new AIOp<Integer>("update", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Integer> _asyncResponseProcessor)
                    throws Exception {
                int oldState = state;
                state = _newState; //assign the new state
                _asyncResponseProcessor.processAsyncResponse(oldState); //return the old state.
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
    AOp<Integer> indirectAOp(final int _newState) {
        return new AOp<Integer>("indirect", getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<Integer> _asyncResponseProcessor)
                    throws Exception {
                //Get a request from the other blades.
                AOp<Integer> req = blade.updateAOp(_newState);

                //Send the request to the other blades.
                _asyncRequestImpl.send(req, new AsyncResponseProcessor<Integer>() {

                    @Override
                    public void processAsyncResponse(Integer response) throws Exception {

                        //Return the old state.
                        _asyncResponseProcessor.processAsyncResponse(response);
                    }
                });
            }
        };
    }
}