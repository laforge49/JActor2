package org.agilewiki.jactor.util.firehose;

public class Load extends StageBase {

    @Override
    public Object process(Engine _engine, Object data) {
        try {
            Thread.sleep(2);
        } catch (InterruptedException ie) {
            try {
                _engine.close();
            } catch (Exception e) {

            }
        }
        return data;
    }
}
