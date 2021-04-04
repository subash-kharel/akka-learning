package part1recap

import scala.concurrent.Future

object ThreadModelLimitations extends App{

  //Scala thread limitations
//limitation 1: oop encapsulation is only valid in the single threaded model
  class BankAccount(private var amount: Int){

    override def toString: String = " "+ amount

    //syncronized
//    def withdraw(money: Int) = this.synchronized{
//      this.amount -= amount
//    }
    def withdraw(money: Int) = this.amount -= money

    //syncronized
    //    def deposit(money: Int) = this.synchronized{
    //      this.amount += amount
    //    }
    def deposit(money: Int) = this.amount += money
    def getAmount = amount

  }

  val account = new BankAccount(2000)
  for(_ <- 1 to 1000){
    new Thread(()=> account.withdraw(1)).start()
  }

  for(_ <- 1 to 1000){
    new Thread(()=> account.deposit(1)).start()
  }

  //This should return 2000 becase it adds 1 and removes 1 and is because these are not snyncronized
  //deadlocks, livelocks
  //OOp encapsulation is broken in a multithreaded env
  //locking is not ideal solution because it slows down complicated apps
  println(account.getAmount)


  // Delegating something to a thread in a pain

  //you have a running thread and you want to pass a runable to that thread

  var task: Runnable = null

  var runningThread: Thread = new Thread(()=> {
    while (true){
      while (task ==null){
        runningThread.synchronized{
          println("waiting for taks")
          runningThread.wait()
        }
      }
      task.synchronized{
        println("i have a task")
        task.run()
        task = null
      }
    }
  })


  //this acts like a produceer so it produces info that could be injected in the consumer above
  def delegateToBackgroundThread(r: Runnable) = {
    if(task == null) task = r

    runningThread. synchronized{
      runningThread.notify()
    }
  }
   runningThread.start()
  Thread.sleep(500)
  delegateToBackgroundThread(()=> println(42))
  Thread.sleep(1000)
  delegateToBackgroundThread(()=> println("This should run in the background"))



  // tracing and dealing with errrors in a multithreaded env in pain

  //1 Mill numbers in between 10 threads
  import scala.concurrent.ExecutionContext.Implicits.global

  val futures = (0 to 9)
    .map(i => 10000 *i until 10000 * (i+1)) //0-99999, 100000- 199999, 20000 -29999 etc
    .map(range => Future{
     // if( range.contains(546735)) throw new RuntimeException("invalid number")
      range.sum
    })

  val sumFuture = Future.reduceLeft(futures)(_ + _) // Future with the sum of all numbers
  sumFuture.onComplete(println)

}
