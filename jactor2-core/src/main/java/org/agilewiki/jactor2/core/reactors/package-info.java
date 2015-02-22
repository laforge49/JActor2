/**
 * <p>Reactors are light-weight threads, or fibers, which manage and process the messages for a set of blades.</p>
 * <p>Reactors are assigned a thread from the plant's thread pool when there are messages to be processed.
 * That is except for thread-bound or swing-bound reactors wich are bound to a given thread.</p>
 * <p>Of particular note is the IsolationReactor class, which processes each request to completion
 * before processing another request.
 * IsolationReactors are also the parents of other reactors, so that reactors are organized into a tree.</p>
 */
package org.agilewiki.jactor2.core.reactors;