package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object AcorsCapabilities extends App{

  class SimpleActor extends  Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello, there" //replying message
      case message: String => println(s"[$self] I have reveived the:  $message")
      case number: Int => println(s"[simple actor] I have received a number : $number")
      case SpecialMessage(contents) => println(s"[simple actor] I have received special content: $contents")
      case SendMessageToYourself(content) =>  self ! content
        //this ref here is bob when we did alice ! sayhi(bob)
      case SayHiTo(ref) => ref ! "Hi"
//      case WirelessPhoneMessage(content, ref) => ref.forward(content)
      case WirelessPhoneMessage(content, ref) => ref forward content
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  //need actor system to get the state inside the actor unlike in java we could instantiate the class
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")
  // ! is also called tell
  simpleActor ! "hello, actor"


  //1) messages can be of any type
       //a) messages must be immutable ( no one can touch the message)
       //b) message must be serializable i.e jvm can transform to byte stream and send it to another machine
  simpleActor ! 42

  case class SpecialMessage(contents:String)
  simpleActor ! SpecialMessage("some special content")

  //2 - actors have info about their contex( similar to this key word in java) and about themselves
  //context.self  == this is oops

  case class SendMessageToYourself(content: String)
  simpleActor ! SendMessageToYourself("I am an actor and I call myself")


  //3 - actors can reply to messsages

  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  //this class has a constructor that takes in Actor reference
  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  //4 - dead letters

  //if we look at the case above it say context.sender but in this case sender is null by default and
  //since this does not have defined sender the message  will be sent to dead letter queue.(Garbage pool)

   alice ! "Hi" //reply to me

  //5 - forwarding messages

  //forwarding = sending message with original sender

  case class WirelessPhoneMessage(content:String, ref: ActorRef)
  alice ! WirelessPhoneMessage("Hi",bob)





}
