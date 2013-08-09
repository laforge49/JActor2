package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Event;

public class ThreadBoundMailboxSample {

    public static void main(String[] args) throws Exception {

        //A context with no threads.
        final JAContext jaContext = new JAContext(0);

        //Get a reference to the main thread.
        final Thread mainThread = Thread.currentThread();

        //Create a thread-bound mailbox.
        final ThreadBoundMailbox boundMailbox =
                new ThreadBoundMailbox(jaContext, new Runnable() {
                    @Override
                    public void run() {
                        //Interrupt the main thread when there are messages to process
                        mainThread.interrupt();
                    }
                });

        //Create an actor that uses the thread-bound mailbox.
        final ThreadBoundActor threadBoundActor = new ThreadBoundActor(boundMailbox);

        //Pass a FinEvent signal to the actor.
        new FinEvent().signal(threadBoundActor);

        //Process messages when this thread is interrupted.
        while (true) {
            try {
                //Wait for an interrupt.
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                //Process messages when the main thread is interrupted.
                boundMailbox.run();
            }
        }
    }
}

class ThreadBoundActor extends ActorBase {

    ThreadBoundActor(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    //Print "finished" and exit when fin is called.
    void fin() throws Exception {
        System.out.println("finished");
        System.exit(0);
    }
}

//When a FinEvent is passed to an actor, the fin method is called.
class FinEvent extends Event<ThreadBoundActor> {
    @Override
    public void processEvent(ThreadBoundActor _targetActor) throws Exception {
        _targetActor.fin();
    }
}
