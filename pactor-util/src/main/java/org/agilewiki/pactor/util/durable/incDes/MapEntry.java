package org.agilewiki.pactor.util.durable.incDes;

import org.agilewiki.pactor.util.durable.ComparableKey;

public interface MapEntry<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends IncDes, ComparableKey<KEY_TYPE> {

    KEY_TYPE getKey();

    VALUE_TYPE getValue();
}
