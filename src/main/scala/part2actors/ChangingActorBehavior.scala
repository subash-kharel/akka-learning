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
      case Food(VEGETABLES) => context.become(sadReceive)//change my receive handler to sadReceive
      case Food(CHOCOLATES) =>
      case Ask(_) => sender ()! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLES)=> //stay sad
      case Food(CHOCOLATES)=>  context.become(happyReceive)//change my receive handler to happyReceive
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
        kidRef ! Ask ("Do you want to play?")
      case KidAccept => println("yay, my kid is happy!")
      case KidReject => println("it is okay my kid is healthy")
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
}
