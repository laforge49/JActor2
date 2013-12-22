import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.misc.SyncPrinterRequest;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class DiningRoom extends NonBlockingBladeBase {
    public DiningRoom(final NonBlockingReactor _reactor)
            throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<List<Integer>> feastAReq(final int _seats, final int _meals)
            throws Exception {
        return new AsyncBladeRequest<List<Integer>>() {
            final AsyncResponseProcessor<List<Integer>> dis = this;
            List<Integer> mealsEaten = new LinkedList<Integer>();
            
            AsyncResponseProcessor<Integer> feastResponseProcessor =
                new AsyncResponseProcessor<Integer>() {
                    @Override
                    public void processAsyncResponse(final Integer _feastResponse) 
                            throws Exception {
                        mealsEaten.add(_feastResponse);
                        if (mealsEaten.size() == _seats) {
                            dis.processAsyncResponse(mealsEaten);
                        }
                    }
            };
            
            @Override
            public void processAsyncRequest() throws Exception {
                int i = 0;
                NonBlockingReactor myReactor = getReactor();
                Facility facility = myReactor.getFacility();
                DiningTable diningTable = new DiningTable(
                    new NonBlockingReactor(facility),
                    _seats,
                    _meals);
                while (i < _seats) {
                    DiningPhilosopher diningPhilosopher =
                        new DiningPhilosopher(new NonBlockingReactor(facility));
                    AsyncRequest<Integer> feastAReq = diningPhilosopher.feastAReq(diningTable, i);
                    send(feastAReq, feastResponseProcessor);
                    ++i;
                }
            }
        };
    }
    
    public static SyncRequest<Void> report(
            final Printer _printer, 
            final int _seats, 
            final int _meals, 
            final List<Integer> _mealsEaten, 
            final long _duration) {
        return new SyncPrinterRequest(_printer) {
            @Override
            public Void processSyncRequest() throws Exception {
                printf("Seats: %,d%n", _seats);
                printf("Meals: %,d%n", _meals);
                println("\nMeals eaten by each philosopher:");
                Iterator<Integer> it = _mealsEaten.iterator();
                int totalEaten = 0;
                while (it.hasNext()) {
                    int me = it.next();
                    totalEaten += me;
                    if (_mealsEaten.size() < 11)
                        printf("    %,d%n", me);
                }
                if (totalEaten != _meals)
                    throw new IllegalStateException("total meals eaten does not match: " + totalEaten);
                printf("\nTest duration in nanoseconds: %,d%n", _duration);
                if (_duration > 0) {
                    printf("Total meals eaten per second: %,d%n%n", 1000000000L * _meals / _duration);
                }
                return null;
            }
        };
    }
    
    public static void main(String[] args) throws Exception {
        int seats = 5;
        int meals = 1000000;
        Plant plant = new Plant();
        try {
            NonBlockingReactor diningRoomReactor = new NonBlockingReactor(plant);
            DiningRoom diningRoom = new DiningRoom(diningRoomReactor);
            AsyncRequest<List<Integer>> feastAReq = diningRoom.feastAReq(seats, meals);
            long before = System.nanoTime();
            List<Integer> mealsEaten = feastAReq.call();
            long after = System.nanoTime();
            long duration = after - before;
            Printer printer = new Printer(new BlockingReactor(plant));
            report(printer, seats, meals, mealsEaten, duration).call();
        } finally {
            plant.close();
        }
    }
}
