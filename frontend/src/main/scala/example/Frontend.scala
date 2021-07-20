package example

import com.raquo.laminar.api.L._
import example.protocol.ExampleService
import typings.airgapBeaconSdk.dappclientoptionsMod.DAppClientOptions
import zio._
import zio.app.DeriveClient
import typings.airgapBeaconSdk.mod.DAppClient

object Frontend {
  val runtime = Runtime.default
  val client  = DeriveClient.gen[ExampleService]

  def view: Div =
    div(
      h3("IMPORTANT WEBSITE"),
      debugView("MAGIC NUMBER", client.magicNumber),
      button(
        "Connect Wallet",
        onClick --> (_ => {
          println("clicked")
          val config = DAppClientOptions("beacon example")
          val client = new typings.airgapBeaconSdk.dappclientMod.DAppClient(config)
        })
      )
    )

  private def debugView[A](name: String, effect: => UIO[A]): Div = {
    val output = Var(List.empty[String])
    div(
      h3(name),
      children <-- output.signal.map { strings =>
        strings.map(div(_))
      },
      onClick --> { _ =>
        runtime.unsafeRunAsync_ {
          effect.tap { a => UIO(output.update(_.prepended(a.toString))) }
        }
      }
    )
  }
}
