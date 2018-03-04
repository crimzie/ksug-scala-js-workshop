package example

import org.scalajs.dom
import upickle.default._
import upickle.{default, Js}
import org.scalajs.dom.html
import scalatags.JsDom.all._
import autowire._

import scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future
import scala.scalajs.js.annotation.JSExport

object Client extends autowire.Client[Js.Value, Reader, Writer] {
  override def doCall(req: Client.Request): Future[Js.Value] = {
    dom.ext.Ajax.post(
      url = "/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq:_*))
    ) map (upickle.json read _.responseText)
  }

  override def read[Result](p: Js.Value)
    (implicit evidence$1: default.Reader[Result]): Result = readJs[Result](p)

  override def write[Result](r: Result)
    (implicit evidence$2: default.Writer[Result]): Js.Value = writeJs(r)
}

@JSExport
object ScalaJSCode {
  @JSExport
  def main(): Unit = {
    val inputBox = input.render
    val outputBox = div.render

    def updateOutput(): Unit = {
      Client[Api].list(inputBox.value).call().foreach { paths =>
        outputBox.innerHTML = ""
        outputBox.appendChild(
          ul(for {path <- paths} yield li(path)).render
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
        h1("File Browser"),
        p("Enter a fille path to s"),
        inputBox,
        outputBox
      ).render
    )
  }
}
