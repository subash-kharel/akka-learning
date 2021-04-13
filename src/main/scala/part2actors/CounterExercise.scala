package part2actors

import akka.actor.{Actor, ActorSystem, Props}


/**
 * Exercise
 * 1. a: counter actor
 *  -Increment
 *  -Decrement
 *  -Print
 *
 *  2.  a Bank account as an actor
 *  receives
 *  -Deposit an amount
 *  -withdraw an amount
 *  -statement
 *  replies with
 *  -Success
 *  -Failure
 *
 *  Interact with some other kind of actor
 *
 *
 * */



object CounterExercise extends App {

  //Domain of the counter
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class CounterActor extends Actor {
    import Counter._
    var count = 0
    override def receive: Receive = {
      case Increment => count +=1
      case Decrement => count -= 1
      case Print => println((s"[counter] My current count is $count"))
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


}
