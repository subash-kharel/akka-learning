package part1recap

import scala.concurrent.Future

object AdvancedRecap extends App{

  //partial functions
  //partial functions are based on patttern matching
  val partialFunction: PartialFunction[Int, Int] = {
    case 1=> 42
    case 2 => 65
    case 5=> 999
  }

  val pf = (x:Int) => x match {
    case 1=> 42
    case 2 => 65
    case 5=> 999
  }


  //lifted
  val lifted = partialFunction.lift //converted to total function Int => Option[Int]
  lifted(2) //some (65)
  lifted(50000) //None

  //orElse
  val pfChain = partialFunction.orElse[Int, Int]{
    case 60 => 9000
  }

  pfChain(5) // this will return 999 per partialfunction
  pfChain(60) // this will return 9000
  pfChain(457) //this throws a Match Error


  //type aliases
  //this is creating an alias of partial function
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("hello")
    case _ => println("confused")
  }

  //implicits


  implicit val timeout = 3000
  def setTimeout(f: () => Unit)(implicit timeout:Int) = f()

  setTimeout(() => println("timeout")) //extra parameter list omitted

  //implici conversion
  //1) implicit defs

  case class Person(name:String){
    def greet = s"Hi, my name is  $name"
  }

  implicit def fromStringToPerson(string: String): Person = Person(string)
  "Peter".greet
  //fromStringToPerson("Peter").greet -- automatically done by compiler


  //2 Implicit classes
  implicit class Dog(name:String){
    def bark = println("bark!")
  }
  "Lassie".bark
  //new Dog("Lassie").bark = automatically done by the compiler

  //organizeo
  //local scope
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_>_)
  List(1,2,3).sorted //List(3,2,1)

  //imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    println("hello future")
  }

  //companion objects of the types included in the call

  object Person {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) =>a.name.compareTo(b.name) < 0)
  }

  List(Person("Bob"), Person("Alice")).sorted
  //THis will give us back List("ALice, Bob)
}
