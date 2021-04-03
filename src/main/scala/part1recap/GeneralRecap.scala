package part1recap

object GeneralRecap extends App{

  //cannot reassign val
  val aCondition: Boolean = false

  //dont need type scala  find the right type for you
  var aVariable = 42
  aVariable += 1 //aVariable = 43

  //expression
  val aConditionedVal = if(aCondition) 42 else 65

  //code block
  val aCodeBlock = {
    if(aCondition) 74
    56
  }

  //types
  //Unit
  val theUnit = println("Hello, Scala")

  def aFunction(x:Int) = x + 1

  //recurion - TAIL recursion
  def factorial(n:Int, acc:Int): Int =
    if(n<=0) acc
    else factorial(n-1, acc *n)

  //OOP
  class Animal

  class Dog extends Animal
  val aDog: Animal = new Dog


  trait Carnivore {
    def eat(a:Animal): Unit
  }


  class Crocodile extends Animal with Carnivore {

    //we have to implement abstract method
  override  def eat(a: Animal): Unit = println("crunch")
  }

  //method notation
  val aCroc = new Crocodile
  aCroc.eat(aDog) //same as: aCroc eat aDog

  //anonymous classes
  //cannot instantiate trait like normal classes we have to implement the method like below
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar")
  }

  aCarnivore.eat(aDog)

  //generics
  //learn this TODO
  abstract class MyList[+A]

  //companion objects
  object MyList

  //case classes
  case class Person(name:String, age:Int)

  //Exceptions
  val aPotentialFailure = try {
    throw new RuntimeException("I am innocent, I swear!")
  } catch {
    case e : Exception => "I caught an exception"
  } finally {
    //side effects
    println("some logs")
  }

  //functional programming
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 +1
  }

  val incremented = incrementer(42) // result = 43

  val anonymousIncrementer = (x:Int) => x+1
  //Int => Int === Function1[Int,Int]

  //FP is all about working with functions as first-class
  List(1,2,3).map(incrementer)
  //map = Higher order functions

  //for comprehensions
  val pairs = for {
    num <- List(1,2,3,4)
    char <- List('a','b','c','d')
  } yield num + "-" + char

  //this is what scala interprets the above logic to:
  // List(1,2,3,4).flatMap(num => List('a','b','c','d').map(char => num + "-" +char))


  //pattern matching
  val unknown = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"

  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _)=> s"Hi, my name is $n"
    case _ => " I dont know my name"
  }

}
