package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{

  //part1- actor systems
  // it is a heavy weight data structure that controls a number of threads under the hood
  //then allocates to running actors
  //it is recommended to have one of these actors system per application unless we have good reason to add many
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  //par2- create actors
  //Actors are uniquely identified
  //messages are process asynchronously
  //each actors may respond differently
  //Actors are encapsulated i.e cannot read the mind of another actor

  //first word count actor
  class WordCountActor extends Actor {
    //internal data
    var totalWords = 0

    //behavior
    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println("[word counter] I have reveived : "+ message   )
        message.split(" ")

      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

  //part3 - instantiate our actor
  //it is good practive to use actors name
  //here variable wordCounter is the actor ref
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")

  //part4- communicate
  //it is invoking the method

  //sending messge here is totally asynchronous
  wordCounter ! "I am learning akka and its pretty cool!"



  //implementing actors for classes with constructor params

  class Person(name: String) extends Actor{
    //receive is a partial function that returns from any to unit
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _=>

    }
  }

  val person = actorSystem.actorOf(Props(new Person("Bob")))

  person.!("hi")

}
