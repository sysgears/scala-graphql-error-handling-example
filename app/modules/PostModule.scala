package modules

import com.google.inject.{AbstractModule, Scopes}
import validators.{PostValidator, PostValidatorImpl}

/**
  * Guice module with bindings related to the Post entity.
  */
class PostModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PostValidator]).to(classOf[PostValidatorImpl]).in(Scopes.SINGLETON)
  }
}