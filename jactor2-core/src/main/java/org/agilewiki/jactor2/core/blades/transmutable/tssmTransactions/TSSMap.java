package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.transmutable.TransmutableSortedMap;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Transmutable Sorted String Map has keys that are strings.
 */
public class TSSMap<VALUE> extends TransmutableSortedMap<String, VALUE> {
    public TSSMap() {
    }

    public TSSMap(Comparator<? super String> comparator) {
        super(comparator);
    }

    public TSSMap(Map<String, VALUE> map) {
        super(map);
    }

    public TSSMap(SortedMap<String, VALUE> map) {
        super(map);
    }

    /**
     * Make an immutable subMap.
     *
     * @param map     A sorted map.
     * @param prefix  The keys in the submap all start with this prefix.
     * @param <VALUE> The type of value
     * @return A subMap.
     */
    public static <VALUE> SortedMap<String, VALUE> subMap(SortedMap<String, VALUE> map, String prefix) {
        map = map.tailMap(prefix);
        return map.headMap(prefix + Character.MAX_VALUE);
    }

    /**
     * Make an immutable subMap.
     *
     * @param prefix The keys in the submap all start with this prefix.
     * @return A subMap.
     */
    public SortedMap<String, VALUE> subMap(String prefix) {
        return subMap(this, prefix);
    }

    @Override
    public TSSMap<VALUE> recreate(SortedMap<String, VALUE> unmodifiable) {
        return new TSSMap<VALUE>(unmodifiable);
    }
}
