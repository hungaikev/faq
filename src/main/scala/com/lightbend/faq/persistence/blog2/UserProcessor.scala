package com.lightbend.faq.persistence.blog2

import akka.persistence.PersistentActor

object UserProcessor {

  case class NewPost(text: String, id: Long)
  case class BlogPosted(id: Long)
  case class BlogNotPosted(id: Long, reason: String)


  sealed trait Event
  case class PostCreated(text: String) extends Event
  case object QuotaReached extends Event


  case class State(posts: Vector[String], disabled: Boolean) {
    def updated(e: Event): State = e match  {
      case PostCreated(text) => copy(posts = posts :+ text)
      case QuotaReached => copy(disabled = true)
    }
  }

}

class UserProcessor extends PersistentActor {
  import UserProcessor._

  var state = State(Vector.empty, false)

  override def persistenceId: String = "userprocessor"

  def updateState(e: Event): Unit = {
    state = state.updated(e)
  }

  override def receiveCommand: Receive = {
    case NewPost(text, id) => if(state.disabled)
      sender() ! BlogNotPosted(id, "quota reached")
    else {
      persist(PostCreated(text)) { e =>
        updateState(e)
        sender() ! BlogPosted(id)
      }
      persist(QuotaReached)(updateState)
    }

  }

  override def receiveRecover: Receive = {
    case e: Event => updateState(e)
  }



}
