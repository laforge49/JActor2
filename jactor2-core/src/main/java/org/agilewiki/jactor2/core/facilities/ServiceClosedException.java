package org.agilewiki.jactor2.core.facilities;

/**
 * This exception is thrown when sending a AsyncRequest to a different facility and that facility is closed.
 * This exception is also thrown when closing a facility that is processing a request from a different facility.
 * This becomes important when working with OSGi and each bundle has its own lifecycle.
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.Facility;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * //Exploring the use of multiple facilities.
 * public class ServiceSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *
 *         //Application facility with 1 thread.
 *         final Facility applicationFacility = new Facility(1);
 *
 *         //Create a service blade that uses its own facility.
 *         Service service = new Service();
 *
 *         try {
 *             //Test the delay echo request on the service blade.
 *             System.out.println(service.delayEchoAReq(1, "1 (Expected)").call());
 *
 *             //close the facility used by the service blade.
 *             service.getReactor().getFacility().close();
 *             try {
 *                 //Try using delay echo request with the facility closed.
 *                 System.out.println(service.delayEchoAReq(1, "(Unexpected)").call());
 *             } catch (ServiceClosedException sce) {
 *                 //The ServiceClosedException is now thrown because the facility is closed.
 *                 System.out.println("Exception as expected");
 *             }
 *
 *             //Create a new service blade that uses its own facility.
 *             service = new Service();
 *             //Create an application blade based on the application facility
 *             //and with a reference to the service blade.
 *             final ServiceApplication serviceApplication =
 *                     new ServiceApplication(service, new NonBlockingReactor(applicationFacility));
 *             //Start a delay echo service request using the application blade.
 *             EchoReqState echoReqState = serviceApplication.echoAReq(1, "2 (Expected)").call();
 *             //Print the results of the delay echo service request.
 *             System.out.println(serviceApplication.echoResultAReq(echoReqState).call());
 *
 *             //Start a second delay echo service request using the application blade.
 *             EchoReqState echoReqState2 = serviceApplication.echoAReq(1, "(Unexpected)").call();
 *             //Close the service facility while the delay echo service request is still sleeping.
 *             serviceApplication.closeServiceAReq().call();
 *             //The results should now show that an exception was thrown.
 *             System.out.println(serviceApplication.echoResultAReq(echoReqState2).call());
 *         } finally {
 *             service.getReactor().getFacility().close(); //Close the service facility.
 *             applicationFacility.close(); //Close the application facility.
 *         }
 *
 *     }
 * }
 *
 * //A service blade that runs on its own facility.
 * class Service extends BladeBase {
 *
 *     Service() throws Exception {
 *         //Create a reactor on a new facility with 1 thread.
 *         initialize(new NonBlockingReactor(new Facility(1)));
 *     }
 *
 *     //Returns a delay echo request.
 *     AsyncRequest&lt;String&gt; delayEchoAReq(final int _delay, final String _text) {
 *         return new AsyncRequest&lt;String&gt;(getReactor()) {
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 //Sleep a bit so that the request does not complete too quickly.
 *                 try {
 *                     Thread.sleep(_delay);
 *                 } catch (InterruptedException e) {
 *                     return;
 *                 }
 *                 //Echo the text back in the response.
 *                 processAsyncResponse("Echo: " + _text);
 *             }
 *         };
 *     }
 *
 * }
 *
 * //Holds the state of a service application echo request.
 * class EchoReqState {
 *     //Not null when an echoResultRequest was received before
 *     // the result of the matching service delay echo request.
 *     AsyncResponseProcessor&lt;String&gt; responseProcessor;
 *
 *     //Not null when the result of the service delay echo request is received
 *     //before the matching echoResultRequest.
 *     String response;
 * }
 *
 * //A blade with a facility that is different than the facility of the service blade.
 * class ServiceApplication extends BladeBase {
 *
 *     //The service blade, which operates in a different facility.
 *     private final Service service;
 *
 *     //Create a service application blade with a reference to a service blade.
 *     ServiceApplication(final Service _service, final Reactor _messageProcessor) throws Exception {
 *         service = _service;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns an application echo request.
 *     //The echo request is used to initiate a service delay echo request.
 *     //And the response returned by the echo request is state data needed to manage the
 *     //delivery of the response from the service delay echo request.
 *     AsyncRequest&lt;EchoReqState&gt; echoAReq(final int _delay, final String _text) {
 *         return new AsyncRequest&lt;EchoReqState&gt;(getReactor()) {
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *
 *                 //State data needed to manage the delivery of the response from
 *                 //the service delay echo request.
 *                 final EchoReqState echoReqState = new EchoReqState();
 *
 *                 //Establish an exception handler which traps a ServiceClosedException and
 *                 //returns a notification that the exception occurred as a result.
 *                 setExceptionHandler(new ExceptionHandler<EchoReqState>() {
 *                     {@literal @}Override
 *                     public EchoReqState processException(Exception exception) throws Exception {
 *                         if (exception instanceof ServiceClosedException) {
 *                             String response = "Exception as expected";
 *                             if (echoReqState.responseProcessor == null) {
 *                                 //No echo result request has yet been received,
 *                                 //so save the response for later.
 *                                 echoReqState.response = response;
 *                             } else {
 *                                 //An echo result request has already been received,
 *                                 //so now is the time to return the response.
 *                                 echoReqState.responseProcessor.processAsyncResponse(response);
 *                             }
 *                             return echoReqState;
 *                         } else
 *                             throw exception;
 *                     }
 *                 });
 *                 service.delayEchoAReq(_delay, _text).send(getReactor(), new AsyncResponseProcessor&lt;String&gt;() {
 *                     {@literal @}Override
 *                     public void processAsyncResponse(String response) throws Exception {
 *                         if (echoReqState.responseProcessor == null) {
 *                             //No echo result request has yet been received,
 *                             //so save the response for later.
 *                             echoReqState.response = response;
 *                         } else {
 *                             //An echo result request has already been received,
 *                             //so now is the time to return the response.
 *                             echoReqState.responseProcessor.processAsyncResponse(response);
 *                         }
 *                     }
 *                 });
 *                 processAsyncResponse(echoReqState);
 *             }
 *         };
 *     }
 *
 *     //Returns a close service request.
 *     AsyncRequest&lt;Void&gt; closeServiceAReq() {
 *         return new AsyncRequest&lt;Void&gt;(getReactor()) {
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 //Close the facility of the service blade.
 *                 service.getReactor().getFacility().close();
 *                 processAsyncResponse(null);
 *             }
 *         };
 *     }
 *
 *     //Returns an echo result request.
 *     //An echo result request returns the response from the service delay echo request
 *     //associated with the given echo request state.
 *     AsyncRequest&lt;String&gt; echoResultAReq(final EchoReqState _echoReqState) {
 *         return new AsyncRequest&lt;String&gt;(getReactor()) {
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 if (_echoReqState.response == null) {
 *                     //There is as yet no response from the associated service delay echo request,
 *                     //so save this request for subsequent delivery of that belated response.
 *                     _echoReqState.responseProcessor = this;
 *                 } else {
 *                     //The response from the associated service delay echo request is already present,
 *                     //so return that response now.
 *                     processAsyncResponse(_echoReqState.response);
 *                 }
 *             }
 *         };
 *     }
 * }
 *
 * Output:
 * Echo: 1 (Expected)
 * Exception as expected
 * Echo: 2 (Expected)
 * Exception as expected
 * </pre>
 */
public class ServiceClosedException extends Exception {
}
