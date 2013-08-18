package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

public class RequestSample {

    public static void main(String[] args) throws Exception {

        //A context with two threads.
        final JAContext jaContext = new JAContext(2);

        try {

            //Create actorA.
            SampleActor2 actorA = new SampleActor2(new NonBlockingMessageProcessor(jaContext));

            //Initialize actorA to 1.
            actorA.updateReq(1).signal();

            //Change actorA to 2.
            System.out.println("was " + actorA.updateReq(2).call() + " but is now 2");

            //Create actorB with a reference to actorA.
            IndirectActor actorB = new IndirectActor(actorA, new NonBlockingMessageProcessor(jaContext));

            //Indirectly change actorA to 42.
            System.out.println("was " + actorB.indirectReq(42).call() + " but is now 42");

        } finally {
            //shutdown the context
            jaContext.close();
        }

    }

}

//A simple actor with state.
class SampleActor2 extends ActorBase {

    //Initial state is 0.
    private int state = 0;

    //Create a SimpleActor2.
    SampleActor2(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    //Return an update request.
    Request<Integer> updateReq(final int _newState) {
        return new Request<Integer>(getMessageProcessor()) {

            @Override
            public void processRequest(Transport<Integer> _transport) throws Exception {
                int oldState = state;
                state = _newState; //assign the new state
                _transport.processResponse(oldState); //return the old state.
            }
        };
    }

}

//An actor which operates on another actor.
class IndirectActor extends ActorBase {

    //The other actor.
    private final SampleActor2 actorA;

    //Create an IndirectActor with a reference to another actor.
    IndirectActor(final SampleActor2 _actorA, final MessageProcessor _messageProcessor) throws Exception {
        actorA = _actorA;
        initialize(_messageProcessor);
    }

    //Return a request to update the other actor and return its new state.
    Request<Integer> indirectReq(final int _newState) {
        return new Request<Integer>(getMessageProcessor()) {

            @Override
            public void processRequest(final Transport<Integer> _transport) throws Exception {

                //Get a request from the other actor.
                Request<Integer> req = actorA.updateReq(_newState);

                //Send the request to the other actor.
                req.send(getMessageProcessor(), new ResponseProcessor<Integer>() {

                    @Override
                    public void processResponse(Integer response) throws Exception {

                        //Return the old state.
                        _transport.processResponse(response);
                    }
                });
            }
        };
    }
}