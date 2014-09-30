package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A transmutable list.
 */
public class TransmutableArrayList<E> extends ArrayList<E> implements Transmutable<List<E>> {
    public TransmutableArrayList() {
    }

    public TransmutableArrayList(int size) {
        super(size);
    }

    public TransmutableArrayList(List<E> list) {
        super(list);
    }

    @Override
    public List<E> createUnmodifiable() {
        return Collections.unmodifiableList(new ArrayList<E>(this));
    }

    @Override
    public TransmutableArrayList<E> recreate(List<E> unmodifiable) {
        return new TransmutableArrayList<>(unmodifiable);
    }
}
