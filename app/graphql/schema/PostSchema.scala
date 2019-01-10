package graphql.schema

import com.google.inject.Inject
import graphql.resolvers.PostResolver
import models.Post
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema._

class PostSchema @Inject()(postResolver: PostResolver) {

  implicit val post: ObjectType[Unit, Post] = deriveObjectType[Unit, Post](ObjectTypeName("Post"))

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "posts",
      fieldType = ListType(post),
      resolve = _ => postResolver.posts
    )
  )

  val Mutations: List[Field[Unit, Unit]] = List(
    Field(
      name = "addPost",
      fieldType = post,
      arguments = List(
        Argument("title", StringType),
        Argument("content", StringType)
      ),
      resolve = sangriaContext =>
        postResolver.addPost(
          sangriaContext.args.arg[String]("title"),
          sangriaContext.args.arg[String]("content")
        )
    )
  )
}