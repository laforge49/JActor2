PActor (POJO Actors)
======

The PActor project will explore the implementation of Java actors implemented without requiring a base class.
Implementation will stress both ease of use and simplicity of implementation.

PActor is a reimplementation of JActor, but without several features:
- There will be only one type of mailbox (asynchronous).
- All message passing will be via the mailbox.
- No message buffering, so there is no need for an outpox. The Mailbox will just be an inbox.
- No factories.
- No message routing to ancestors.

Features of JActor to be preserved:
- Both 1-way (events) and 2-way (request/response) messaging will be included.
- Exception handlers.

[Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
