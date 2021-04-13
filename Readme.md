Valid questions:

Can we assume any ordering of messages?

Are we causing race conditions?

what does asynchronous actually mean for actors?

How does this all actor magic work?

--->Akka has a thread pool that it shares with actors.
Actor: actor has message handeler( thing we define in recive method)
actor also has message queue.

actor is just a Data structure and it needs a thread to run.


Akka has 10's--100's threads  and each threads can handle huge number of actors and the
way akka manages to do that is by scheduling actors for execution on these
small number of threads.

Communication Mechanism:

Sending a message:
-- message is enqued in the actors mailbox
-- thread safe( akka handles this)

Processing messages:
-- a thread is scheduled to run this actor
-- messages are extracted from the mailbox in order
-- the thread invokes the handler on each message
-- after everything is processed this thread is unscheduled for the actor and
that thread can do something else.


Gurantess:
--Only one thread operates on an actor at any time which makes actors effecively
  single threaded and no locks needed. Only one thread has access to the actors
  internal state at time. A thread may never release actor middle of procesing
    messages. This is atomic
-- Message delivery guarantess
   akka offer at most once message delivery gurantess never recives duplicates.
   -- message order is kept
   --for any sender-receiver pair, the message order is maintained
Example: If alice sends Bob message A followed by B:
    1) Bob will never receive duplicates of A or B
    2) Bob will always receive A before B
