package graphql

import com.google.inject.{Inject, Singleton}
import graphql.schema.PostSchema
import models.errors.{InvalidTitle, TooComplexQueryError}
import sangria.execution.{ExceptionHandler, HandledException, MaxQueryDepthReachedError}
import sangria.schema.{ObjectType, fields}
import sangria.validation.UndefinedFieldViolation
import sangria.marshalling.MarshallingUtil._

/**
  * Defines the global GraphQL schema of the application.
  */
@Singleton
class GraphQL @Inject()(val postSchema: PostSchema) {

  val maxQueryDepth = 15
  val maxQueryComplexity = 1000

  val Schema = sangria.schema.Schema(
    query = ObjectType("Query",
      fields(
        postSchema.Queries: _*
      )
    ),
    mutation = Some(
      ObjectType("Mutation",
        fields(
          postSchema.Mutations: _*
        )
      )
    )
  )

  /**
    * Exception handler that defines custom error handling mechanism.
    */
  val exceptionHandler = ExceptionHandler(
    onException = {
      case (resultMarshaller, error: InvalidTitle) => HandledException(
        error.getMessage,
        Map(
          "validation_rule" -> resultMarshaller.fromString("The following symbols are allowed: [a-z,A-Z,0-9,- ]. From 3 to 100 symbols.")
        ),
        addFieldsInError = true,
        addFieldsInExtensions = false
      )
      case (_, error: TooComplexQueryError) => HandledException(error.getMessage)
      case (_, error: MaxQueryDepthReachedError) => HandledException(error.getMessage)
    },
    onViolation = {
      case (resultMarshaller, violation: UndefinedFieldViolation) =>
        HandledException(
          "Field is missing!",
          Map(
            "fieldName" -> resultMarshaller.fromString(violation.fieldName),
            "errorCode" -> resultMarshaller.fromString("FIELD_MISSING")
          )
        )
    }
  )
}