package graphql.resolvers

import com.google.inject.Inject
import models.Post
import repositories.PostRepository

import scala.concurrent.{ExecutionContext, Future}

class PostResolver @Inject()(val postRepository: PostRepository,
                             implicit val executionContext: ExecutionContext) {

  def posts: Future[List[Post]] = postRepository.findAll()

  def addPost(title: String, content: String): Future[Post] = postRepository.create(Post(title = title, content = content))
}