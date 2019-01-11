package graphql.resolvers

import com.google.inject.Inject
import models.Post
import repositories.PostRepository
import validators.PostValidator

import scala.concurrent.{ExecutionContext, Future}

class PostResolver @Inject()(val postRepository: PostRepository,
                             val postValidator: PostValidator,
                             implicit val executionContext: ExecutionContext) {

  import postValidator._

  def posts: Future[List[Post]] = postRepository.findAll()

  def addPost(title: String, content: String): Future[Post] = {
    withTitleValidation(title) {
      postRepository.create(Post(title = title, content = content))
    }
  }

  def findPost(id: Long): Future[Option[Post]] = postRepository.find(id)

  def updatePost(id: Long, title: String, content: String): Future[Post] = postRepository.update(Post(Some(id), title, content))

  def deletePost(id: Long): Future[Boolean] = postRepository.delete(id)
}