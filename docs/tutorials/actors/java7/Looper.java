abstract class Looper<RESPONSE_TYPE> extends BasicActor {

    abstract protected void iterate(Reply<RESPONSE_TYPE> reply);
    
    private Reply<RESPONSE_TYPE> externalReply;
    private RESPONSE_TYPE response;
    private volatile boolean replied;
    private volatile boolean done;
    
    private Reply<RESPONSE_TYPE> reply = new Reply<RESPONSE_TYPE>() {
        public void response(RESPONSE_TYPE _response) {
            start();
            response = _response;
            replied = true;
            boolean d = done;
            finish();
            
            if (d) {
                if (response == null)
                    loop();
                else
                    externalReply.response(response);
            }
        }
    };
    
    private void loop() {
        while (true) {
            response = null;
            replied = false;
            done = false;
            
            iterate(reply);
            
            start();
            done = true;
            boolean r = replied;
            RESPONSE_TYPE rsp = response;
            finish();
            
            if (!r)
                return;
            else if (rsp != null) {
                externalReply.response(rsp);
                return;
            }
        }
    }

    void go(final Reply<RESPONSE_TYPE> _externalReply) {
        externalReply  = _externalReply;
        loop();
    }
}
