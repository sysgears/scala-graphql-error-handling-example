package graphql

import com.google.inject.{Inject, Singleton}
import graphql.schema.PostSchema
import models._
import sangria.schema.{ObjectType, Schema, fields}

@Singleton
class GraphQL @Inject()(val personSchema: PostSchema) {

  val maxQueryDepth = 15
  val maxQueryComplexity = 1000

  val Schema = sangria.schema.Schema(
    query = ObjectType("Query",
      fields(
        personSchema.Queries: _*
      )
    ),
    mutation = Some(
      ObjectType("Mutation",
        fields(
          personSchema.Mutations: _*
        )
      )
    )
  )
}