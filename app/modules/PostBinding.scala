package modules

import com.google.inject.{AbstractModule, Provides, Scopes}
import models.Post
import repositories.{PostRepository, Repository}
import validators.{PostValidator, PostValidatorImpl}

import scala.concurrent.ExecutionContext

/**
  * Guice module with bindings related to the Post entity.
  */
class PostBinding extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PostValidator]).to(classOf[PostValidatorImpl]).in(Scopes.SINGLETON)
  }

  @Provides
  def providePostRepository(implicit executionContext: ExecutionContext): Repository[Post] = new PostRepository()
}