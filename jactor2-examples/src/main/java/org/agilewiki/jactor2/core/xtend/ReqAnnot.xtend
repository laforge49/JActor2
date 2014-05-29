package org.agilewiki.jactor2.core.xtend

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase
import org.agilewiki.jactor2.core.xtend.codegen.SReq
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.xtend.codegen.AReq

class ReqAnnot extends NonBlockingBladeBase {

    new() throws Exception {
    }

	@SReq
	private def String hello(String yourName) {
		"hello, "+yourName+"!"
	}

	@SReq
	private def void nop() {

	}

	@SReq
	private def int meaning() {
		42
	}

	@AReq
	private def void hello(AsyncRequest<String> ar, String yourName) {
		ar.processAsyncResponse("hello, "+yourName+"!")
	}

	@AReq
	private def void nop(AsyncRequest<Void> ar) {
		ar.processAsyncResponse(null)
	}

	@AReq
	private def void meaning(AsyncRequest<Integer> ar) {
		ar.processAsyncResponse(42)
	}
}
