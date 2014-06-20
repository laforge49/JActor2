/*
 * Copyright (C) 2014 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agilewiki.jactor2.annotations.xtend

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.eclipse.xtend.lib.macro.Active
import org.agilewiki.jactor2.annotations.xtend.codegen.SReqProcessor
import org.agilewiki.jactor2.annotations.xtend.codegen.AReqProcessor

/**
 * Marks an instance method that should be wrapped in a SyncRequest.
 *
 * Generates the method returning a SyncRequest for an instance method, as
 * well as a "direct call" public method.
 *
 * Example:
 *
 * <code>
    // User written
    @SReq
    private long _ping() {
        count += 1;
        return count;
    }

    // Generated!
    public SyncRequest<Long> pingSReq() {
        return new SyncBladeRequest<Long>() {
            @Override
            public Long processSyncRequest() {
                return _ping();
            }
        };
    }
    public long ping(final Reactor sourceReactor) {
        directCheck(sourceReactor);
        return _ping();
    }
    </code>
 *
 * @author monster
 */
@Active(typeof(SReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation SReq {
}

/**
 * Marks an instance method that should be wrapped in a AsyncRequest.
 *
 * Generates the method returning a AsyncRequest for an instance method, as
 * well as a "direct call" public method.
 *
 * Example:
 *
 * <code>
    // User written
    @AReq
    private void _hang(AsyncRequest<Void> ar) {
        // NOP
    }

    // Generated!
    public AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                _hang(this);
            }
        };
    }
    public void hang(final AsyncRequest<Void> ar) {
        // Might get NPE here ...
        directCheck(ar.getTargetReactor());
        _hang(ar);
    }
    </code>
 *
 * @author monster
 */
@Active(typeof(AReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation AReq {
}
