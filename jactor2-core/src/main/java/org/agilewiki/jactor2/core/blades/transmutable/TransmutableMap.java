package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A transmutable map.
 */
public class TransmutableMap<K, V> extends HashMap<K, V> implements Transmutable<Map<K, V>> {
    public TransmutableMap() {
    }

    public TransmutableMap(int size) {
        super(size);
    }

    public TransmutableMap(Map map) {
        super(map);
    }

    @Override
    public Map<K, V> createUnmodifiable() {
        return Collections.unmodifiableMap(new HashMap(this));
    }

    @Override
    public TransmutableMap<K, V> recreate(Map<K, V> map) {
        return new TransmutableMap<K, V>(map);
    }
}
