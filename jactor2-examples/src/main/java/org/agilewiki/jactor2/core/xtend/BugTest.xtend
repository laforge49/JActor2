package org.agilewiki.jactor2.core.xtend

interface ResponseProcessor {
	def void processAsyncResponse()
}

abstract class Request {
	def void processAsyncResponse() {
		// NOP
	}
}

class Bug {
	def newRequest() {
		new Request() {
			val dis = this

			val proc = new ResponseProcessor() {
				override void processAsyncResponse() {
					// THIS PRODUCES BAD CODE
					dis.processAsyncResponse()
				}
			}
		}
	}
}
