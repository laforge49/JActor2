import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class DiningRoom extends NonBlockingBladeBase {
    public AsyncRequest<List<Integer>> feastAReq(final int _seats, final int _meals) {
        return new AsyncBladeRequest<List<Integer>>() {
            final AsyncRequest<List<Integer>> dis = this;
            List<Integer> mealsEaten = new LinkedList<Integer>();
            
            AsyncResponseProcessor<Integer> feastResponseProcessor =
                new AsyncResponseProcessor<Integer>() {
                    @Override
                    public void processAsyncResponse(final Integer _feastResponse) {
                        mealsEaten.add(_feastResponse);
                        if (mealsEaten.size() == _seats) {
                            dis.processAsyncResponse(mealsEaten);
                        }
                    }
            };
            
            @Override
            public void processAsyncRequest() {
                int i = 0;
                DiningTable diningTable = new DiningTable(
                    _seats,
                    _meals);
                while (i < _seats) {
                    DiningPhilosopher diningPhilosopher =
                        new DiningPhilosopher();
                    AsyncRequest<Integer> feastAReq = diningPhilosopher.feastAReq(diningTable, i);
                    send(feastAReq, feastResponseProcessor);
                    ++i;
                }
            }
        };
    }
    
    public static void main(String[] args) throws Exception {
        int seats = 5;
        int meals = 1000000;
        new Plant();
        try {
            DiningRoom diningRoom = new DiningRoom();
            AsyncRequest<List<Integer>> feastAReq = diningRoom.feastAReq(seats, meals);
            long before = System.nanoTime();
            List<Integer> mealsEaten = feastAReq.call();
            long after = System.nanoTime();
            long duration = after - before;
            System.out.println("Seats: " + seats);
            System.out.println("Meals: " + meals);
            System.out.println("\nMeals eaten by each philosopher:");
            Iterator<Integer> it = mealsEaten.iterator();
            int totalEaten = 0;
            while (it.hasNext()) {
                int me = it.next();
                totalEaten += me;
                if (mealsEaten.size() < 11)
                    System.out.println("    " + me);
            }
            if (totalEaten != meals)
                throw new IllegalStateException("total meals eaten does not match: " + totalEaten);
            System.out.println("\nTest duration in nanoseconds: " + duration);
            if (duration > 0) {
                System.out.println("Total meals eaten per second: " + (1000000000L * meals / duration));
            }
        } finally {
            Plant.close();
        }
    }
}
