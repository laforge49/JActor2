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
package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.requests.impl.RequestImplWithData;

/**
 * API for a payload-carrying native asynchronous request.
 *
 * @author monster
 */
public interface AsyncNativeRequestWithData<RESPONSE_TYPE> extends
        AsyncNativeRequest<RESPONSE_TYPE>, RequestImplWithData<RESPONSE_TYPE> {

}
