package example

import java.io.File

import fs2.StreamApp
import monix.eval.Task
import monix.eval.Task.{catsAsync, catsEffect}
import monix.execution.Scheduler.Implicits.global
import org.http4s.{HttpService, MediaType, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import upickle.Js
import upickle.default._

object Template {

  import scalatags.Text.all._
  import scalatags.Text.tags2.title

  val txt: String = "<!DOCTYPE html" +
    html(
      head(
        title("Karazin Scala Users' Group"),
        meta(httpEquiv := "Content-Type", content := "text/html; charset=UTF-8"),
        script(`type` := "text/javascript", src := "/client-fastopt.js"),
        link(
          rel := "stylesheet",
          `type` := "text/css",
          href := "css/bootstrap.min.css")),
      body(margin := 0)(script("example.ScalaJSCode().main()")))
}

object Controller extends Api {
  override def list(path: String): Seq[String] = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.dropRight(1).mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }
}

object AutowireServer extends autowire.Server[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value): Result = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result): Js.Value = upickle.default.writeJs(r)
}

object Server extends StreamApp[Task] with Http4sDsl[Task] {
  val service: HttpService[Task] = HttpService[Task] {
    case GET -> Root                                       =>
      Response[Task](Ok).withBody(Template.txt).withType(MediaType.`text/html`)
    case request@GET -> Root / "client-fastopt.js"         =>
      StaticFile
        .fromFile(
          new File("/Users/crimson/IdeaProjects/scalajs/root/js/target/scala-2.12/client-fastopt.js"),
          Some(request))
        .getOrElse(Response[Task](NotFound))
    case request@GET -> Root / "css" / "bootstrap.min.css" =>
      scribe.info("Got request")
      StaticFile
        .fromResource(
          "/META-INF/resources/webjars/bootstrap/4.0.0-1/css/bootstrap.min.css",
          Some(request))
        .getOrElse(Response[Task](NotFound))
    case request@POST -> path                              =>
      for {
        jv <- request.as[String] map upickle.json.read
        js <- Task deferFuture {
          AutowireServer.route[Api](Controller)(autowire.Core.Request[Js.Value](
            path.toList,
            jv.asInstanceOf[Js.Obj].value.toMap))
        }
        rs <- Response[Task](Ok).withBody(js.toString).withType(MediaType.`application/json`)
      } yield rs
  }

  override def stream(
      args: List[String],
      requestShutdown: Task[Unit],
  ): fs2.Stream[Task, StreamApp.ExitCode] =
    BlazeBuilder[Task]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
