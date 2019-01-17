package modules

import com.google.inject.{AbstractModule, Provides, Scopes}
import models.Post
import repositories.{PostRepository, Repository}
import validators.{PostValidator, PostValidatorImpl}

import scala.concurrent.ExecutionContext

/**
  * The Guice module with bindings related to the Post entity.
  */
class PostBinding extends AbstractModule {

  /**
    * A method where bindings should be defined.
    */
  override def configure(): Unit = {
    bind(classOf[PostValidator]).to(classOf[PostValidatorImpl]).in(Scopes.SINGLETON)
  }

  /**
    * Provides an implementation of the Repository[Post] trait.
    *
    * @param executionContext a thread pool to asynchronously execute operations
    * @return an instance of the PostRepository class that implements Repository[Post] trait
    */
  @Provides
  def providePostRepository(implicit executionContext: ExecutionContext): Repository[Post] = new PostRepository()
}