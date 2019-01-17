package graphql.resolvers

import com.google.inject.Inject
import models.Post
import repositories.Repository
import validators.PostValidator

import scala.concurrent.{ExecutionContext, Future}

/**
  * A resolver that does actions on the Post entity.
  *
  * @param postRepository   a repository that provides basic operations for the Post entity
  * @param postValidator    a validator that contains functions that validates the Post's fields
  * @param executionContext a thread pool to asynchronously execute operations
  */
class PostResolver @Inject()(val postRepository: Repository[Post],
                             val postValidator: PostValidator,
                             implicit val executionContext: ExecutionContext) {

  import postValidator._

  /**
    * Finds all posts.
    *
    * @return a list of a posts
    */
  def posts: Future[List[Post]] = postRepository.findAll()

  /**
    * Adds a post.
    *
    * @param title   a title of the post
    * @param content a content of the post
    * @return added post
    */
  def addPost(title: String, content: String): Future[Post] = {
    withTitleValidation(title) {
      postRepository.create(Post(title = title, content = content))
    }
  }

  /**
    * Finds a post by id.
    *
    * @param id an id of the post
    * @return found post
    */
  def findPost(id: Long): Future[Option[Post]] = postRepository.find(id)

  /**
    * Updates a post.
    *
    * @param id      an id of the post
    * @param title   a title of the post
    * @param content a content of the post
    * @return updated post
    */
  def updatePost(id: Long, title: String, content: String): Future[Post] =
    postRepository.update(Post(Some(id), title, content))

  /**
    * Deletes a post by id.
    *
    * @param id an id of the post
    * @return true if the post was deleted, else otherwise
    */
  def deletePost(id: Long): Future[Boolean] = postRepository.delete(id)
}