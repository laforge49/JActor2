package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A transmutable list.
 */
public class TransmutableArrayList<E> extends ArrayList<E> implements Transmutable<List<E>> {
    protected volatile List<E> unmodifiable;

    public TransmutableArrayList() {
        createUnmodifiable();
    }

    public TransmutableArrayList(int size) {
        super(size);
        createUnmodifiable();
    }

    public TransmutableArrayList(List<E> list) {
        super(list);
        createUnmodifiable();
    }

    @Override
    public List<E> getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = Collections.unmodifiableList(new ArrayList<E>(this));
    }

    @Override
    public Transmutable<List<E>> recover() {
        return new TransmutableArrayList<>(unmodifiable);
    }
}
