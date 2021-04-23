package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


/*
   Exercises
   1) Recreate the couter actor with context become with no mutable state
   2) simplified voting system
 */
object ChangingActorBehaviorExercise  extends App {

  //1
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class CounterActor extends Actor {
    import Counter._
    override def receive: Receive = countReceive(0)

    def countReceive(currentCount: Int): Receive = {
      case Increment =>
        println("Incrementing-->"+ currentCount)
        context.become(countReceive(currentCount +1))
      case Decrement =>
        println("Decrementing--->"+ currentCount)
        context.become(countReceive(currentCount -1))
      case Print => println(s"[Counter] my current count is $currentCount")
    }
    }


  //creating an actor system, this is relevent to creating a new instance in java
  //counter is now the actor reference
  var system = ActorSystem("counterActor")
  var counter = system.actorOf(Props[CounterActor], "counter")
  import Counter._
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print


 // 2 a simplified voting system

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {
    override def receive: Receive = awaitingCommand

    def awaitingCommand: Receive = {
      case AggregateVotes(citizens) =>
          citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
          context.become(awaitingStatus(citizens,Map()))
    }

    def awaitingStatus(stillWaiting: Set[ActorRef], currentStats: Map[String, Int]): Receive = {

      case VoteStatusReply(None) =>
        sender() ! VoteStatusRequest
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
       val  newStats = currentStats + (candidate -> (currentVotesOfCandidate +1))
        if(newStillWaiting.isEmpty){
          println(s"[aggregators poll stats: ${newStats}")
        } else {
          context.become(awaitingStatus(newStillWaiting, newStats))
        }
    }

  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jones")
  charlie ! Vote("Ronald")
  daniel ! Vote("Ronald")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice,bob,charlie,daniel))

  /*
  print the sattus of the votes

  Martin ->1
  JOnas -> 1
  Ronald -> 2

   */

}
