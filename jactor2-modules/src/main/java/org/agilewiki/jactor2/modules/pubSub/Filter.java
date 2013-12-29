package org.agilewiki.jactor2.modules.pubSub;

/**
 * Identifies content of interest.
 *
 * @param <CONTENT> The type of content.
 */
public interface Filter<CONTENT> {
    /**
     * Returns true when the content is of interest.
     *
     * @param _content The content to be judged.
     * @return True when the content is of interest.
     */
    boolean match(CONTENT _content);
}
