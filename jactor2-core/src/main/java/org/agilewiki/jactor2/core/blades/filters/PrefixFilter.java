package org.agilewiki.jactor2.core.blades.filters;

/**
 * Selects strings with a given prefix
 */
public class PrefixFilter implements Filter<String> {
    public final String prefix;

    public PrefixFilter(final String _prefix) {
        prefix = _prefix;
    }

    @Override
    public boolean match(final String _content) {
        if (_content == null)
            return false;
        return _content.startsWith(prefix);
    }
}
