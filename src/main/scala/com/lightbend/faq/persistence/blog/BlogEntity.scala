package com.lightbend.faq.persistence.blog

import akka.actor.{ActorLogging, Props}
import akka.pattern._
import akka.persistence.{PersistentActor, SnapshotOffer}

import scala.concurrent.{Future, Promise}

object BlogEntity {

  def props = Props(new BlogEntity)

  sealed trait BlogCommand

  final case class GetPost(id: PostId) extends BlogCommand
  final case class AddPost(content: PostContent) extends BlogCommand
  final case class UpdatePost(id: PostId, content: PostContent) extends BlogCommand

  sealed trait  BlogEvent {
    val id: PostId
    val content: PostContent
  }

  final case class PostAdded(id: PostId, content: PostContent) extends BlogEvent
  final case class PostUpdated(id: PostId, content: PostContent) extends BlogEvent
  final case class PostNotFound(id: PostId) extends RuntimeException(s"Blog post not found with id $id")

  type MaybePost[+A] = Either[PostNotFound, A]

  final case class BlogState(posts: Map[PostId, PostContent]) {
    def apply(id: PostId): MaybePost[PostContent] = posts.get(id).toRight(PostNotFound(id))

    def + (event: BlogEvent): BlogState = BlogState(posts.updated(event.id, event.content))

  }

  object BlogState {
    def apply(): BlogState = BlogState(Map.empty)
  }


}

class BlogEntity extends PersistentActor with ActorLogging {

  import BlogEntity._
  import context._

  private var state = BlogState()

  // we need to create an actor for every blog post, though that
  // sounds a bit overkill to me. then our persistenceId would be something like s"blog-$id".
  override def persistenceId: String = "blog"


  private def handleEvent[E <: BlogEvent] (e: => E) : Future[E] = {
    val p = Promise[E]
    persist(e) { event =>
      p.success(event)
      state += event
      system.eventStream.publish(event)
      if(lastSequenceNr != 0 && lastSequenceNr % 1000 == 0)
        saveSnapshot(state)
    }
    p.future
  }


  override def preStart(): Unit = log.info(s"Blog with id ' ${persistenceId} ' has been started ")

  override def postStop(): Unit = log.info(s"Blog with id '${persistenceId}' has been stopped ")


  override def receiveCommand: Receive = {
    case GetPost(id) =>
      sender() ! state(id)

    case AddPost(content) =>
      handleEvent(PostAdded(PostId(), content)) pipeTo sender()
      ()

    case UpdatePost(id, content) =>
      state(id) match {
        case response @ Left (_) => sender() ! response
        case Right(_) => handleEvent(PostUpdated(id, content)) pipeTo sender()
          ()
      }
  }

  override def receiveRecover: Receive = {
    case event: BlogEvent => state += event
    case SnapshotOffer(_, snapshot: BlogState) => state = snapshot
  }

}
