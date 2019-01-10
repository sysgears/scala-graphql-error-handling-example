package repositories

import com.google.inject.Inject

import scala.collection.mutable
import models.Post
import models.errors.AlreadyExists

import scala.concurrent.{ExecutionContext, Future}

class PostRepository @Inject()(implicit val executionContext: ExecutionContext) extends Repository[Post] {

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
      _ => Future.failed(AlreadyExists(s"Post with title: ${post.title} already exists."))
    }
  }

  /** @inheritdoc*/
  override def find(id: Long): Future[Option[Post]] = Future.successful {
    postCollection.find(_.id == id)
  }

  /** @inheritdoc*/
  override def findAll(): Future[List[Post]] = Future.successful {
    postCollection.toList
  }

  /** @inheritdoc*/
  override def update(post: Post): Future[Post] = synchronized {
    find(post.id.get).flatMap {
      case Some(foundPost) =>
        val updatedPost = foundPost.copy()
        val foundPostIndex = postCollection.indexWhere(_.id == post.id)

        postCollection(foundPostIndex) = updatedPost

        Future.successful(updatedPost)
      case _ => Future.failed(new Exception(s"Not found Person with id=${post.id}"))
    }
  }

  /** @inheritdoc*/
  override def delete(id: Long): Future[Boolean] = Future.successful {
    synchronized {
      postCollection.indexWhere(_.id == id) match {
        case -1 => false
        case personIndex =>
          postCollection.remove(personIndex)
          true
      }
    }
  }
}
