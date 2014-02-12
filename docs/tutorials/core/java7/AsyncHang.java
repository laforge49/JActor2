import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class AsyncHang extends NonBlockingBladeBase {

    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            AsyncHang asyncHang = new AsyncHang();
            asyncHang.hangAReq().call();
        } finally {
            Plant.close();
        }
    }

    public AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() {
                //no response--the request hangs
            }
        };
    }
}
