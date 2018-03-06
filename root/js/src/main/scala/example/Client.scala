package example

import autowire._
import org.scalajs.dom
import upickle.{default, Js}
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

object Client extends autowire.Client[Js.Value, Reader, Writer] {
  override def doCall(req: Client.Request): Future[Js.Value] =
    dom.ext.Ajax
      .post(
        url = "/" + req.path.mkString("/"),
        data = upickle.json.write(Js.Obj(req.args.toSeq: _*)))
      .map(upickle.json read _.responseText)

  override def read[Result](p: Js.Value)
    (implicit r: default.Reader[Result]): Result = readJs[Result](p)

  override def write[Result](r: Result)
    (implicit w: default.Writer[Result]): Js.Value = writeJs(r)
}

@JSExport
object ScalaJSCode {
  @JSExport
  def main(): Unit = {
    val inputBox = input.render
    val outputBox = div.render

    def updateOutput(): Unit = {
      Client[Api].list(inputBox.value).call().foreach { path =>
        outputBox.innerHTML = ""
        outputBox.appendChild(
          h1(path).render
        )
      }
    }

    inputBox.onkeyup = {
      e: dom.Event => updateOutput()
    }

    updateOutput()

    dom.document.body.appendChild(
      div(
        cls := "container",
        p("Enter a file path to flip"),
        inputBox,
        outputBox
      ).render
    )
  }
}
