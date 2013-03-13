PActor (POJO Actors)
======

The PActor project will explore the implementation of Java actors implemented without requiring a base class.
Implementation will stress both ease of use and simplicity of implementation.

PActor is a reimplementation of JActor, but without several features:
- There will be only one type of mailbox (asynchronous).
- All message passing will be via the mailbox.
- No message buffering, so there is no need for an outbox. The Mailbox will just have an inbox.
- No factories.
- No message routing to ancestors.
- Instead of reimplementing JAThreadManager, the slightly slower ThreadPoolExecutor.

Features of JActor to be preserved:
- Both 1-way (events) and 2-way (request/response) messaging will be included.
- Exception handlers.

One new feature will be added in this project: the elimination of the need for a base class for actors. 
Any Plain Old Java Object (POJO) can function as an actor, given only a reference to a mailbox.
And there are no interfaces that need to be implemented either.

Status: The API is complete, though untested and undocumented. And *only* two methods in MailboxImpl remain todo in the implementation.

[Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
