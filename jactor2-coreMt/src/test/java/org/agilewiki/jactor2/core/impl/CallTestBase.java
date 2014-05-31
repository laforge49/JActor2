package org.agilewiki.jactor2.core.impl;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.requests.Request;

/**
 * Test code base class.
 */
public class CallTestBase extends TestCase {
    /** Implemented differently in coreSt(), since call() does not exist there. */
    protected <RESPONSE_TYPE> RESPONSE_TYPE call(
            final Request<RESPONSE_TYPE> request) throws Exception {
        return request.call();
    }
}
