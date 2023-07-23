package app

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer

@js.native
@JSGlobal
object APPSTREAMDUMPS extends js.Object {

  def put(key: String, vall: String | ArrayBuffer): Promise[Unit] = js.native

  def get(key: String): Promise[String] = js.native

  def get(key: String, options: js.Dynamic): Promise[ArrayBuffer] = js.native
  
}
