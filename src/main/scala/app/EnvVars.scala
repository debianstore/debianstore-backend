package app

import app.OptionExtensions.getOrThrow

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope

object EnvVars {

  val EXTERNAL_BASE_URL =
      RealEnvVars.EXTERNAL_BASE_URL
      .toOption
      .flatMap(vall => if (vall == null) then Option.empty else Some(vall) )
      .getOrThrow(() => Exception("EXTERNAL_BASE_URL is not defined but must be"))

  val ACCESS_CONTROL_ALLOW_ORIGIN =
      RealEnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
      .toOption
      .flatMap(vall => if (vall == null) then Option.empty else Some(vall) )
      .getOrThrow(() => Exception("ACCESS_CONTROL_ALLOW_ORIGIN is not defined but must be"))

}


@js.native
@JSGlobalScope
object RealEnvVars extends js.Object {
  var EXTERNAL_BASE_URL: js.UndefOr[String] = js.native

  var ACCESS_CONTROL_ALLOW_ORIGIN: js.UndefOr[String] = js.native
}
