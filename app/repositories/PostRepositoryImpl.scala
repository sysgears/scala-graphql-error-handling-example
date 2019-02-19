package repositories

import com.google.inject.{Inject, Singleton}
import models.Post
import models.errors.{AlreadyExists, AmbigousResult, NotFound}
import modules.AppDatabase

import scala.concurrent.{ExecutionContext, Future}

/**
  * Provides basic CRUD operations on the Post entity.
  * @param database a database access object
  * @param executionContext a thread pool to asynchronously execute operations
  */
@Singleton
class PostRepositoryImpl @Inject()(val database: AppDatabase,
                                   implicit val executionContext: ExecutionContext) extends PostRepository {

  /**
    * Specific database
    */
  val db = database.db

  /**
    * Specific database profile
    */
  val profile = database.profile

  import profile.api._

  def postQuery: TableQuery[Post.Table] = TableQuery[Post.Table]

  /** @inheritdoc */
  override def create(post: Post): Future[Post] = db.run {
    Actions.create(post)
  }

  /** @inheritdoc */
  override def find(id: Long): Future[Option[Post]] = db.run {
    Actions.find(id)
  }

  /** @inheritdoc */
  override def findAll(): Future[List[Post]] = db.run {
    Actions.findAll()
  }

  /** @inheritdoc */
  override def update(post: Post): Future[Post] = db.run {
    Actions.update(post)
  }

  /** @inheritdoc */
  override def delete(id: Long): Future[Boolean] = db.run {
    Actions.delete(id)
  }

  /**
    * Provides implementation for CRUD operations on the Post entity.
    */
  object Actions {

    def create(post: Post): DBIO[Post] = for {
      maybePost <- if (post.id.isEmpty) DBIO.successful(None) else find(post.id.get)
      _ <- maybePost.fold(DBIO.successful()) {
          _ => DBIO.failed(AlreadyExists(s"Post with id = ${post.id} already exists."))
        }
      postWithSameTitle <- postQuery.filter(_.title === post.title).result
      id <- if (postWithSameTitle.lengthCompare(1) < 0) postQuery returning postQuery.map(_.id) += post else {
          DBIO.failed(AlreadyExists(s"Post with title = '${post.title}' already exists."))
        }
      } yield post.copy(id = Some(id))

    def find(id: Long): DBIO[Option[Post]] = for {
      maybePost <- postQuery.filter(_.id === id).result
      post <- if (maybePost.lengthCompare(2) < 0) DBIO.successful(maybePost.headOption) else {
        DBIO.failed(AmbigousResult(s"Several posts with the same id = $id"))
      }
    } yield post

    def findAll(): DBIO[List[Post]] = postQuery.result.map(_.toList)

    def update(post: Post): DBIO[Post] = for {
      maybePostWithSameTitle <- postQuery.filter(_.title === post.title).result
      _ <- if (maybePostWithSameTitle.lengthCompare(1) < 0) DBIO.successful() else {
        DBIO.failed(AlreadyExists(s"Post with title='${post.title}' already exists."))
      }
      count <- postQuery.filter(_.id === post.id).update(post)
      _ <- count match {
        case 0 => DBIO.failed(NotFound(s"Can't find post with id=${post.id}."))
        case _ => DBIO.successful(())
      }
    } yield post

    def delete(id: Long): DBIO[Boolean] = for {
      deleteCount <- postQuery.filter(_.id === id).delete
      isDeleted = if (deleteCount == 1) true else false
    } yield isDeleted
  }

}
