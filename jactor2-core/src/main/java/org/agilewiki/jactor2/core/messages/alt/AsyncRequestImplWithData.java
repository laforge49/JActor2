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
package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;

/**
 * API for a payload-carrying asynchronous request.
 *
 * @author monster
 */
public interface AsyncRequestImplWithData<RESPONSE_TYPE> extends
        AsyncRequestImpl<RESPONSE_TYPE>, RequestImplWithData<RESPONSE_TYPE> {

}
