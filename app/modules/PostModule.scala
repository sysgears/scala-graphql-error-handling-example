package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.{PostRepository, PostRepositoryImpl}
import validators.{PostValidator, PostValidatorImpl}


/**
  * The Guice module with bindings related to the Post entity.
  */
class PostModule extends AbstractModule {

  /**
    * A method where bindings should be defined.
    */
  override def configure(): Unit = {
    bind(classOf[PostValidator]).to(classOf[PostValidatorImpl]).in(Scopes.SINGLETON)
    bind(classOf[PostRepository]).to(classOf[PostRepositoryImpl]).in(Scopes.SINGLETON)
  }
}