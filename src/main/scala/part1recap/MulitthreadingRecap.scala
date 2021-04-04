package part1recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MulitthreadingRecap extends App{

  //Creatign threads on the JVM


  //creating threads
  val aThread = new Thread(() => println("I am running in parallel"))
  //this to start the thread
  aThread.start()
  //wait for the thread to finish
  aThread.join()

  val threadHello = new Thread(()=> (1 to 1000). foreach(_ => "hello"))
  val threadGoodBye = new Thread(()=> (1 to 1000). foreach(_ => "goodbye"))

  //different run produces different result
  threadHello.start()
  threadGoodBye.start()

//  class BankAccount( @volatile private var amount: Int){
  //locks amount for read and write and only works for premitive type Int
  //this helps some in syncronization
  class BankAccount(private var amount: Int){

    override def toString: String = " "+ amount

    //this is not thread safe because:

    /* lets say we have a Bank Balance: 1000-

    Thread1 ---> withdraw 1000
    Thread2 -----> withdraw 2000

    Thread1--> this.amount = this.amount - .... //prempted by the os
    Thread2---> this.amount = this.amount - 2000 = 8000
    Thread1--> -1000 = 9000

    => Resount = 900-
    this.amount = this.amount -1000 is NOt atomic

    */

    def withdraw(money: Int) = this.amount -= money


    //doing syncronized one thread has to be blocked before evaluating an expression
    //so hence is thread safe

    def safeWithdraw(money: Int) = this.synchronized {
      this.amount -= money
    }
  }

  //inter-thread communication on the JVM
  //wait - notify

  //scala futures
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    // long computation - on a different thread
    42
  }

  //callbacks
  future.onComplete{
    case Success(42) => println("I found the meaning of life")
    case Failure(_) => println("something happened")
  }

  val aProcessedFuture = future.map(_+1) // future with 43
  val aFlatFuture = future.flatMap{
    value => Future(value + 2)
      //future with 44
  }

  val filteredFuture = future.filter(_ %2 ==0) //NoSuchElementException


  //for comprehensions
  val aNonsenseFuture= for {
    meaningOfLife <- future
    filteredMeaning <- filteredFuture
  } yield meaningOfLife + filteredMeaning


}
