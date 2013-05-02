package org.agilewiki.jactor.util.durable.incDes;

public interface MapEntry<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends IncDes {

    KEY_TYPE getKey()
            throws Exception;

    VALUE_TYPE getValue()
            throws Exception;
}
