package org.agilewiki.jactor2.core.impl;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.SOp;

/**
 * Test code base class.
 */
public class CallTestBase extends TestCase {
    /** Implemented differently in coreSt(), since call() does not exist there. */
    protected <RESPONSE_TYPE> RESPONSE_TYPE call(
            final AOp<RESPONSE_TYPE> operation) throws Exception {
        return operation.call();
    }

    /** Implemented differently in coreSt(), since call() does not exist there. */
    protected <RESPONSE_TYPE> RESPONSE_TYPE call(
            final SOp<RESPONSE_TYPE> operation) throws Exception {
        return operation.call();
    }
}
