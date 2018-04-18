package com.lightbend.faq.core

import akka.actor.{Actor, ActorRef, FSM, Props}

import scala.concurrent.duration._


// states
sealed trait State
case object Idle extends State
case object Active extends State

sealed trait Data
case object Uninitialized extends Data
final case class Todo(target: ActorRef, queue: collection.immutable.Seq[Any]) extends Data



object Buncher {

  def props: Props = Props(new Buncher)

  // SetTarget is needed for starting it up, setting the destination for the Batches to be passed on
  final case class SetTarget(ref: ActorRef)

  // Queue will add to the internal queue
  final case class Queue(obj: Any)

  // Flush will mark the end of a burst
  case object Flush

  // sent events
  final case class Batch(obj: collection.immutable.Seq[Any])

}

class Buncher extends Actor with FSM[State, Data] {

  /**
    * The actor can be in two states: no message queued (aka Idle) or some message queued (aka Active).
    * It will stay in the Active state as long as messages keep arriving and no flush is requested.
    */

  import Buncher._

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(SetTarget(ref), Uninitialized) ⇒
      stay using Todo(ref, Vector.empty)
  }


  onTransition {
    case Active -> Idle ⇒
      stateData match {
        case Todo(ref, queue) ⇒ ref ! Batch(queue)
        case _                ⇒ // nothing to do
      }
  }

  when(Active, stateTimeout = 1 second) {
    case Event(Flush | StateTimeout, t: Todo) ⇒
      goto(Idle) using t.copy(queue = Vector.empty)
  }

  whenUnhandled {
    // common code for both states
    case Event(Queue(obj), t @ Todo(_, v)) ⇒
      goto(Active) using t.copy(queue = v :+ obj)

    case Event(e, s) ⇒
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  initialize()


}
