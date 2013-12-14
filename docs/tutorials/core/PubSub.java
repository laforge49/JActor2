import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.pubSub.IsInstanceFilter;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.blades.pubSub.Subscription;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

public class PubSub {
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Printer printer = Printer.stdoutAReq(plant).call();
            RequestBus<Object> requestBus =
                    new RequestBus<Object>(new NonBlockingReactor(plant));
            Subscriber subscriber =
                    new Subscriber(new NonBlockingReactor(plant), requestBus, printer);
            subscriber.listenAReq().call();
            Publisher publisher =
                    new Publisher(new NonBlockingReactor(plant), requestBus);
            publisher.goAReq().call();
        } finally {
            plant.close();
        }
    }
}

class Publisher extends BladeBase {
    final RequestBus<Object> requestBus;

    Publisher(final NonBlockingReactor _reactor, final RequestBus<Object> _requestBus) 
            throws Exception {
        initialize(_reactor);
        requestBus = _requestBus;
    }
    
    AsyncRequest<Void> goAReq() {
        return new AsyncBladeRequest<Void>() {
            protected void processAsyncRequest() throws Exception {
                requestBus.signalsContentSReq("start").signal();
                requestBus.signalsContentSReq(1).signal();
                requestBus.signalsContentSReq(2).signal();
                requestBus.signalsContentSReq(3).signal();
                send(requestBus.sendsContentAReq("done"), this);
            }
        };
    }
}

class Subscriber extends BladeBase {
    final RequestBus<Object> requestBus;
    final Printer printer;
    int state;

    Subscriber(final NonBlockingReactor _reactor, final RequestBus<Object> _requestBus, final Printer _printer) 
            throws Exception {
        initialize(_reactor);
        requestBus = _requestBus;
        printer = _printer;
    }

    AsyncRequest<Void> listenAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor dis = this;

            protected void processAsyncRequest() throws Exception {
            
                new SubscribeAReq<Object>(requestBus,
                        (NonBlockingReactor) getReactor(),
                        new IsInstanceFilter<Object>(Integer.class)) {
                    @Override
                    protected void processContent(Object _content, AsyncRequest<Void> arp)
                            throws Exception {
                        int oldState = state;
                        int inc = (Integer) _content;
                        state = state + inc;
                        arp.send(printer.printlnSReq("" + oldState + " + " + inc + " = " + state), 
                            new AsyncResponseProcessor<Void>() {
                                @Override
                                public void processAsyncResponse(Void rsp) throws Exception {
                                    arp.processAsyncResponse(null);
                                }
                            });
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
