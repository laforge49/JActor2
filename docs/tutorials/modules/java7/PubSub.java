import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.Printer;
import org.agilewiki.jactor2.modules.pubSub.IsInstanceFilter;
import org.agilewiki.jactor2.modules.pubSub.RequestBus;
import org.agilewiki.jactor2.modules.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.modules.pubSub.Subscription;

public class PubSub {
    public static void main(final String[] _args) throws Exception {
        new MPlant();
        try {
            Printer printer = Printer.stdoutAReq().call();
            RequestBus<Object> requestBus =
                    new RequestBus<Object>(new NonBlockingReactor());
            Subscriber subscriber =
                    new Subscriber(new NonBlockingReactor(), requestBus, printer);
            subscriber.listenAReq().call();
            Publisher publisher =
                    new Publisher(new NonBlockingReactor(), requestBus);
            publisher.goAReq().call();
        } finally {
            Plant.close();
        }
    }
}

class Publisher extends NonBlockingBladeBase {
    final RequestBus<Object> requestBus;

    Publisher(final NonBlockingReactor _reactor, final RequestBus<Object> _requestBus) 
            throws Exception {
        super(_reactor);
        requestBus = _requestBus;
    }
    
    AsyncRequest<Void> goAReq() {
        return new AsyncBladeRequest<Void>() {
            public void processAsyncRequest() throws Exception {
                requestBus.signalsContentSReq("start").signal();
                requestBus.signalsContentSReq(1).signal();
                requestBus.signalsContentSReq(2).signal();
                requestBus.signalsContentSReq(3).signal();
                send(requestBus.sendsContentAReq("done"), this);
            }
        };
    }
}

class Subscriber extends NonBlockingBladeBase {
    final RequestBus<Object> requestBus;
    final Printer printer;
    int state;

    Subscriber(final NonBlockingReactor _reactor, final RequestBus<Object> _requestBus, final Printer _printer) 
            throws Exception {
        super(_reactor);
        requestBus = _requestBus;
        printer = _printer;
    }

    AsyncRequest<Void> listenAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor dis = this;

            public void processAsyncRequest() throws Exception {
            
                new SubscribeAReq<Object>(requestBus,
                        getReactor(),
                        new IsInstanceFilter<Object>(Integer.class)) {
                    @Override
                    protected void processContent(Object _content, AsyncRequest<Void> arp)
                            throws Exception {
                        int oldState = state;
                        int inc = (Integer) _content;
                        state = state + inc;
                        arp.send(printer.printlnSReq("" + oldState + " + " + inc + " = " + state), arp);
                    }
                }.signal();
                
                send(new SubscribeAReq<Object>(requestBus,
                             (NonBlockingReactor) getReactor(),
                             new IsInstanceFilter<Object>(String.class)) {
                         @Override
                         protected void processContent(Object _content, AsyncRequest<Void> arp)
                                 throws Exception {
                            arp.send(printer.printlnSReq((String) _content), arp);
                         }
                }, dis, null);

            }
        };
    }
}
