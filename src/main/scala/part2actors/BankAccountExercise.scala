package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.BankAccountExercise.Person.LiveTheLife


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


object BankAccountExercise extends App {


  object BankAccount {
    case class Deposit(amount:Int)
    case class Withdraw(amount: Int)
    case object Statement

    case class TransactionSuccess(message: String)
    case class TransactionFailure( reason: String)
  }
  class BankAccountActor extends Actor{
    import BankAccount._
    var funds = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if( amount < 0) sender() ! TransactionFailure("Invalid deposit amount")
        else {
          funds += amount
          sender()  ! TransactionSuccess(s"Successfully deposited $amount")
        }
      case Withdraw(amount) =>
        if( amount <0) sender() ! TransactionFailure("Invalid withdraw amount")
        else if (amount > funds){
          sender() ! TransactionFailure("insufficient fund")
        } else {
          funds -= amount
          sender() !TransactionSuccess(s"successfully withdrawn $amount")
        }
      case Statement => sender() ! s"Your balance is $funds"

    }

  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends  Actor{
    import Person._
    import BankAccount._
    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(1000)
        account ! Withdraw(9000)
        account ! Withdraw(500)
        account ! Statement
      case message => println(message.toString)

    }
  }

  var system = ActorSystem("bankAccount")
  val account = system.actorOf(Props[BankAccountActor], "bankAccountActor")
  val person = system.actorOf(Props[Person], "personActor")

  person ! LiveTheLife(account)


}
