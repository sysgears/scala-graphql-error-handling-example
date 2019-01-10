package modules

import com.google.inject.{AbstractModule, Scopes}
import validators.{PostValidator, PostValidatorImpl}

class PostModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PostValidator]).to(classOf[PostValidatorImpl]).in(Scopes.SINGLETON)
  }
}