import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class Loops extends ActorBase {
    public static void main(final String[] _args) 
            throws Exception {
        ModuleContext moduleContext = new ModuleContext();
        try {
		    Loops loops = new Loops(new NonBlockingMessageProcessor(moduleContext));
			Sums sums;
			/*
			System.out.println("\nno thread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(new ModuleContext()));
			loops.test(100000, sums);
		    sums.getMessageProcessor().getModuleContext().close();
			*/
			System.out.println("\nthread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(moduleContext));
			loops.test(1000000, sums);
        } finally {
            moduleContext.close();
        }
    }
	
	Loops(final MessageProcessor _messageProcessor)
            throws Exception {
	    initialize(_messageProcessor);
	}
	
	void test(final long _c, final Sums _sums) throws Exception {
			System.out.println("1 => " + loopReq(_sums, 1).call());
		    _sums.clearReq().signal();
			System.out.println("2 => " + loopReq(_sums, 2).call());
		    _sums.clearReq().signal();
			System.out.println("100 => " + loopReq(_sums, 100).call());
		    _sums.clearReq().signal();

			System.gc();
			long t0 = System.currentTimeMillis();
			long r = loopReq(_sums, _c).call();
			long t1 = System.currentTimeMillis();
			long d = t1 - t0;
			System.out.println("" + _c + " => " + r + " in " + d + " milliseconds");
			if (d > 0)
			    System.out.println("" + (_c * 1000 / d) + " requests/responses per second");
		    _sums.clearReq().signal();
	}
	
	Request<Long> loopReq(final Sums _sums, final long _count) {
        return new Request<Long>(getMessageProcessor()) {
		    long counter;
			Transport<Long> transport;
			ResponseProcessor<Long> responseProcessor = new ResponseProcessor<Long>() {
			    @Override
				public void processResponse(final Long _response) throws Exception {
				    if (counter == 1) {
					    transport.processResponse(_response);
					} else {
					    counter -= 1;
        			    _sums.addReq(counter).send(getMessageProcessor(), responseProcessor);
					}
				}
			};
			
            @Override
            public void processRequest(final Transport<Long> _transport) 
                    throws Exception {
				if (_count < 1) {
				    _transport.processResponse(0L);
				} else {
				    counter = _count;
					transport = _transport;
		            ModuleContext moduleContext = getMessageProcessor().getModuleContext();
    			    _sums.addReq(counter).send(getMessageProcessor(), responseProcessor);
				}
            }
        };
	}
}

class Sums extends ActorBase {
    private long total = 0;
	
	Sums(final MessageProcessor _messageProcessor)
            throws Exception {
	    initialize(_messageProcessor);
	}
	
	Request<Void> clearReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Void> _transport) 
                    throws Exception {
				total = 0L;
                _transport.processResponse(null);
            }
        };
	}
	
	Request<Long> addReq(final long _value) {
        return new Request<Long>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Long> _transport) 
                    throws Exception {
				total += _value;
                _transport.processResponse(total);
            }
        };
	}
}