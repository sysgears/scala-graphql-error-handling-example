package graphql

import com.google.inject.{Inject, Singleton}
import graphql.schema.PostSchema
import models.errors.{InvalidTitle, TooComplexQueryError}
import sangria.execution.{ExceptionHandler, HandledException, MaxQueryDepthReachedError}
import sangria.schema.{ObjectType, fields}
import sangria.validation.UndefinedFieldViolation

/**
  * Defines the global GraphQL schema of the application.
  */
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

  /**
    * Exception handler that defines custom error handling mechanism.
    */
  val exceptionHandler = ExceptionHandler(
    onException = {
      case (m, error: InvalidTitle) => HandledException(
        error.getMessage,
        Map(
          "validation rule" -> m.scalarNode("The following symbols are allowed: a-z,A-Z,0-9,-", "String", Set.empty)
        )
      )
      case (_, error@TooComplexQueryError) => HandledException(error.getMessage)
      case (_, error@MaxQueryDepthReachedError(_)) => HandledException(error.getMessage)
    },
    onViolation = {
      case (m, v: UndefinedFieldViolation) =>
        HandledException("Field is missing!",
          Map(
            "fieldName" → m.scalarNode(v.fieldName, "String", Set.empty),
            "errorCode" → m.scalarNode("FIELD_MISSING", "String", Set.empty))
        )
    }
  )
}