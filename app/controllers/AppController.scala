package controllers

import com.google.inject.{Inject, Singleton}
import graphql.GraphQL
import models.errors.{TooComplexQueryError, UnsupportedBodyTypeError}
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.Document
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
  *
  * @param controllerComponents a base controller components dependencies that most controllers rely on.
  * @param graphQL              an object that
  */
@Singleton
class AppController @Inject()(controllerComponents: ControllerComponents,
                              graphQL: GraphQL) extends AbstractController(controllerComponents) {

  /**
    * Renders a page with an in-browser IDE for exploring GraphQL.
    */
  def graphiql: Action[AnyContent] = Action(Ok(views.html.graphiql()))

  /**
    * Parses GraphQL body of incoming request and executes GraphQL query.
    *
    * @return an 'Action' that handles a request and generates a result to be sent to the client
    */
  def graphqlBody: Action[JsValue] = Action.async(parse.json) {
    implicit request: Request[JsValue] =>

      val extract: JsValue => (String, Option[String], Option[JsObject]) = query => (
        (query \ "query").as[String],
        (query \ "operationName").asOpt[String],
        (query \ "variables").toOption.flatMap {
          case JsString(vars) => Some(parseVariables(vars))
          case obj: JsObject => Some(obj)
          case _ => None
        }
      )

      val maybeQuery: Try[(String, Option[String], Option[JsObject])] = Try {
        request.body match {
          case arrayBody@JsArray(_) => extract(arrayBody.value(0))
          case objectBody@JsObject(_) => extract(objectBody)
          case otherType =>
            throw UnsupportedBodyTypeError(
              s"/graphql endpoint does not support request body of type [${otherType.getClass.getSimpleName}]"
            )
        }
      }

      maybeQuery match {
        case Success((query, operationName, variables)) => executeQuery(query, variables, operationName)
        case Failure(error) => Future.successful {
          BadRequest(error.getMessage)
        }
      }
  }

  /**
    * Renders a GraphQL schema.
    */
  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema {
      graphQL.Schema
    })
  }

  /**
    * Parses variables of incoming query.
    *
    * @param variables variables from incoming query
    * @return JsObject with variables
    */
  def parseVariables(variables: String): JsObject = if (variables.trim.isEmpty || variables.trim == "null") Json.obj()
  else Json.parse(variables).as[JsObject]

  /**
    * Analyzes and executes incoming GraphQL query and returns the execution result.
    *
    * @param query     GraphQL body of request
    * @param variables incoming variables passed in the request
    * @param operation a name of the operation (queries or mutations)
    * @return simple result which defines the response header and the body to send to the client
    */
  def executeQuery(query: String, variables: Option[JsObject] = None, operation: Option[String] = None): Future[Result] = QueryParser.parse(query) match {
    case Success(queryAst: Document) => Executor.execute(
      schema = graphQL.Schema,
      queryAst = queryAst,
      variables = variables.getOrElse(Json.obj()),
      exceptionHandler = graphQL.exceptionHandler,
      queryReducers = List(
        QueryReducer.rejectMaxDepth[Unit](graphQL.maxQueryDepth),
        QueryReducer.rejectComplexQueries[Unit](graphQL.maxQueryComplexity, (_, _) => TooComplexQueryError())
      )
    ).map(Ok(_)).recover {
      case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
      case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
    }
    case Failure(ex) => Future(BadRequest(s"${ex.getMessage}"))
  }
}