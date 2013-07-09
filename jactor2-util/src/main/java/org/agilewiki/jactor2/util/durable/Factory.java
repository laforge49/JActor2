package org.agilewiki.jactor2.util.durable;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.Named;

/**
 * For every type of serializable object there is a factory object that creates and
 * configures objects of that type, with the name assigned to the factory object identifying
 * the type of serializable object.
 */
public interface Factory extends Named {

    /**
     * Creates and configures a serializable object.
     *
     * @param _mailbox A mailbox--must not be null.
     * @return The sew serializable object.
     */
    JASerializable newSerializable(final Mailbox _mailbox) throws Exception;

    /**
     * Creates and configures a serializable object.
     *
     * @param _mailbox A mailbox--must not be null.
     * @param _parent  The dependency to be injected.
     * @return The sew serializable object.
     */
    JASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent) throws Exception;

    /**
     * Returns a string that uniquely identifies the type of serializable object across
     * all name spaces.
     *
     * @return A string in the form factoryName|bundleName|bundleVersion.
     */
    String getFactoryKey();
}
