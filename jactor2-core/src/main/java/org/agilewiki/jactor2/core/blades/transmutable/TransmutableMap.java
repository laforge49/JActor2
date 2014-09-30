package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A transmutable map.
 */
public class TransmutableMap<K, V> extends HashMap<K, V> implements Transmutable<Map<K, V>> {
    protected Map unmodifiable;

    public TransmutableMap() {
        createUnmodifiable();
    }

    public TransmutableMap(int size) {
        super(size);
        createUnmodifiable();
    }

    public TransmutableMap(Map map) {
        super(map);
        createUnmodifiable();
    }

    @Override
    public Map<K, V> getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = Collections.unmodifiableMap(new HashMap(this));
    }

    @Override
    public Transmutable<Map<K, V>> recover() {
        return null;
    }
}
