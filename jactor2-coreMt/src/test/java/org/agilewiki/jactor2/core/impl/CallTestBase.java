package org.agilewiki.jactor2.core.impl;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.requests.Operation;

/**
 * Test code base class.
 */
public class CallTestBase extends TestCase {
    /** Implemented differently in coreSt(), since call() does not exist there. */
    protected <RESPONSE_TYPE> RESPONSE_TYPE call(
            final Operation<RESPONSE_TYPE> operation) throws Exception {
        return operation.call();
    }
}
