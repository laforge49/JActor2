package org.agilewiki.jactor.util.durable.incDes;

public interface Root extends Box {

    public static final String FACTORY_NAME = "root";

    String getDescriptor()
            throws Exception;
}
