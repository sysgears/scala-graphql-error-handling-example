package repositories

import com.google.inject.Inject

import scala.collection.mutable
import models.Post
import models.errors.{AlreadyExists, NotFound}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Provides basic operations on the Post entity.
  *
  * @param executionContext a thread pool to asynchronously execute operations
  */
class PostRepository @Inject()(implicit val executionContext: ExecutionContext) extends Repository[Post] {

  /**
    * Collection of the Post entities.
    */
  val postCollection: mutable.ArrayBuffer[Post] = mutable.ArrayBuffer.empty[Post]

  /** @inheritdoc*/
  override def create(post: Post): Future[Post] = synchronized {
    postCollection.find(_.title == post.title).fold {
      Future {
        val newPost = post.copy(
          id = {
            val allIds = postCollection.flatMap(_.id)
            if (allIds.nonEmpty) Some(allIds.max + 1L) else Some(1L)
          }
        )
        postCollection += newPost
        newPost
      }
    } {
      _ => Future.failed(AlreadyExists(s"Post with title='${post.title}' already exists."))
    }
  }

  /** @inheritdoc*/
  override def find(id: Long): Future[Option[Post]] = Future.successful {
    postCollection.find(_.id.contains(id))
  }

  /** @inheritdoc*/
  override def findAll(): Future[List[Post]] = Future.successful {
    postCollection.toList
  }

  /** @inheritdoc*/
  override def update(post: Post): Future[Post] = synchronized {
    post.id match {
      case Some(id) =>
        find(id).flatMap {
          case Some(_) =>
            val foundPostIndex = postCollection.indexWhere(_.id == post.id)
            postCollection(foundPostIndex) = post
            Future.successful(post)
          case _ => Future.failed(NotFound(s"Can't find post with id=${post.id}."))
        }
      case _ => Future.failed(NotFound("Post's id wasn't provided."))
    }
  }

  /** @inheritdoc*/
  override def delete(id: Long): Future[Boolean] = Future.successful {
    synchronized {
      postCollection.indexWhere(_.id.contains(id)) match {
        case -1 => false
        case personIndex =>
          postCollection.remove(personIndex)
          true
      }
    }
  }
}
