Programming for Thread-Safety & Performance
======

As the number of cores continue to increase, it becomes harder and harder to write software
that makes effective use of those cores. There are two challenges here.

1. The first challenge is to avoid race conditions. This is usually quite difficult as race conditions
are often quite subtle, hard to spot and impossible to test for.

2. The second challenge is to write code that uses multiple threads that runs faster than single-threaded
code in any but the simplest of cases. Locks cause context switches that result in memory caches having to
be reloaded. And any data passed between threads similarly must be reloaded into the new thread's memory
cache.

JActor2 is lock-free code that extends the actor programming paradigm for increased effectiveness
and ease of use. And its API makes it easy to confirm that the application code is indeed free of
race conditions.

- [Documentation](http://laforge49.github.io/JActor2/docs/index.html)
- [Downloads](http://laforge49.github.io/JActor2/downloads)
- [Google Group](https://groups.google.com/forum/?hl=en&fromgroups#!forum/agilewikidevelopers)
- Compatible with [Maven 3](http://maven.apache.org/).
- License: [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
