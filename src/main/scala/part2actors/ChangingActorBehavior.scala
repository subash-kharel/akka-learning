package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App{


  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }
  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    //internal state of the kid
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLES) => state = SAD
      case Food(CHOCOLATES) => state = HAPPY
      case Ask(message) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }


  class StatelessFussyKid extends Actor{
    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLES) => context.become(sadReceive, false)//change my receive handler to sadReceive
      case Food(CHOCOLATES) =>
      case Ask(_) => sender ()! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLES)=> context.become(sadReceive, false) //stay sad
      case Food(CHOCOLATES)=>  context.unbecome()
      case Ask(_) => sender() ! KidReject
    }
  }

  //companion object mom... recommended way to include the case classes
  object Mom{
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message:String) // do you want to play?
    val VEGETABLES ="veggies"
    val CHOCOLATES = "chocolate"
  }

  class Mom extends Actor {
    import Mom._
    import FussyKid._
    override def receive: Receive = {
      case MomStart(kidRef) =>
        //test our interaction
        kidRef ! Food(VEGETABLES)
        kidRef ! Food(VEGETABLES)
        kidRef ! Food(CHOCOLATES)
        kidRef ! Ask ("Do you want to play?")
      case KidAccept => println("yay, my kid is happy!")
      case KidReject => println("it is okay my kid is healthy even he is sad")
    }
  }

  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])

//   mom ! MomStart(fussyKid)


  //creating stateless fussy kid
  mom ! MomStart(statelessFussyKid)
  /*
  mom receives MomStart
      kid receives Food(veg) -> kid will change the handler to sadReceive
      kid receives Ask(play?) -> kid replies with the sad Receive handler =>
      mom receives KidReject
   */


  /*
     mom receives MomStart ( initially: stack: 1. happyReceive
     kid reeives Food(veg) ---> Stack : 1. sadReceive
                                        2. happyReceive
     kid receives Food(veg) again -----> Stack : 1. sadReceive
                                                 2. sadReceive
                                                 3. happyReceive
     kid receives Food(chocolate) then ------> Stack : 1. sadReceive ( popped the first one)
                                                       2. happyReceive
     since the top of the stack is sadReceive it will go inside the case class for sadReceive
   */

  /*
  context.become(anotherHandler, true)
  anotherHandler: must be of type Receive
  boolean: replaces current handler(default), pass in false to stack the new handler on top

  Reverting to the previous behavior: context.become() ---> pops the current behavior off the stack

  Rules: Akka always uses the latest handler on top of the stack.
   */
}
