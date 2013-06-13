package org.agilewiki.jactor3;

/**
 * A Request is sent to another Actor and subsequently returned with a response.
 */
public interface Request<TARGET extends Actor, RESPONSE> extends Message<TARGET> {

    /**
     * Calls a method on the target actor to process the Request.
     *
     * @param _targetActor The actor that the request was sent to.
     * @return A message to be sent to another actor, or null.
     */
    Message processRequest(final TARGET _targetActor);

    /**
     * Assigns the response to be processed by the requesting Actor.
     *
     * @param _response The response to be processed.
     */
    void setResponse(final RESPONSE _response);

    /**
     * A callback to process the response within the context of the requesting Actor.
     *
     * @param _response The response to be processed.
     */
    void processResponse(final RESPONSE _response);
}
