package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{Inject, Singleton}
import graphql.GraphQL
import models.errors.{InvalidTitle, TooComplexQueryError}
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.{Document, Field}
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.QueryParser
import sangria.renderer.SchemaRenderer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class AppController @Inject()(cc: ControllerComponents,
                              env: play.Environment,
                              config: Configuration,
                              graphql: GraphQL) extends AbstractController(cc) {

  implicit val system: ActorSystem = ActorSystem()

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def graphiql = if (env.isDev || env.isTest) Action(Ok(views.html.graphiql())) else Action(NotFound)

  def graphqlBody = Action.async(parse.json) {
    implicit request: Request[JsValue] =>
      //TODO: Front-end provides an array of graphql objects as batch. Use Sangria batch executor here.

      val extract: JsValue => (String, Option[String], Option[JsObject]) = query => (
        (query \ "query").as[String],
        (query \ "operationName").asOpt[String],
        (query \ "variables").toOption.flatMap {
          case JsString(vars) => Some(parseVariables(vars))
          case obj: JsObject => Some(obj)
          case _ ⇒ None
        }
      )

      //TODO: Define a separate type for it
      val maybeQuery: Try[(String, Option[String], Option[JsObject])] = Try {
        request.body match {
          case arrayBody@JsArray(_) => extract(arrayBody.value(0))
          case objectBody@JsObject(_) => extract(objectBody)
          case otherType =>
            //TODO: Define custom type for this error
            throw new Error {
              s"/graphql endpint does not support request body of type [${otherType.getClass.getSimpleName}]"
            }
        }
      }

      maybeQuery match {
        case Success((query, operationName, variables)) => executeQuery(query, variables, operationName)
        case Failure(error) => Future.successful {
          BadRequest(error.getMessage)
        }
      }
  }

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema {
      graphql.Schema
    })
  }

  def parseVariables(variables: String): JsObject = if (variables.trim.isEmpty || variables.trim == "null") Json.obj()
  else Json.parse(variables).as[JsObject]

  def executeQuery(query: String, variables: Option[JsObject] = None, operation: Option[String] = None): Future[Result] = QueryParser.parse(query) match {
    case Success(queryAst: Document) => Executor.execute(
      schema = graphql.Schema,
      queryAst = queryAst,
      variables = variables.getOrElse(Json.obj()),
      exceptionHandler = exceptionHandler,
      queryReducers = List(
        QueryReducer.rejectMaxDepth[Unit](graphql.maxQueryDepth),
        QueryReducer.rejectComplexQueries[Unit](graphql.maxQueryComplexity, (_, _) => TooComplexQueryError)
      )
    ).map(Ok(_)).recover {
//      case error: QueryReducingError => Forbidden(error.resolveError)
      case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
      case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
    }
    case Failure(ex) => Future.successful(Ok(s"${ex.getMessage}"))
  }

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error@TooComplexQueryError) ⇒ HandledException(error.getMessage)
    case (_, error@MaxQueryDepthReachedError(_)) ⇒ HandledException(error.getMessage)
  }
}