package org.agilewiki.jactor2.core.blades.pubSub;

/**
 * Used to select a particular content of interest.
 *
 * @param <CONTENT> The type of content.
 */
public class EqualsFilter<CONTENT> implements Filter<CONTENT> {
    private final CONTENT selection;

    /**
     * Create an EqualsFilter.
     *
     * @param _selection The particular content of interest.
     */
    public EqualsFilter(final CONTENT _selection) {
        selection = _selection;
    }

    @Override
    public boolean match(Object _content) {
        if (selection == null)
            return _content == null;
        return selection.equals(_content);
    }
}
