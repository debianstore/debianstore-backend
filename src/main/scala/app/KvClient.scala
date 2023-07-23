package app

import app.OptionExtensions.getOrThrow
import org.scalajs.dom.Blob

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object KvClient {

  def ALL_PKGS_KEY = "ALL_PKGS"

  def put(key: String, vall: String): Future[Unit] = {
    APPSTREAMDUMPS.put(key, vall).toFuture
  }

  def get(key: String): Future[String] = {
    APPSTREAMDUMPS.get(key)
      .toFuture
      .map(vall => {
        Option(vall).getOrThrow(() => Exception(s"value for key ${key} not found"))
      })
  }

  def putImage(image: String, content: ArrayBuffer): Future[Unit] = {
    APPSTREAMDUMPS.put(
      s"IMAGE_${image}",
      content
    )
    .toFuture
  }

  def getImage(image: String): Future[ArrayBuffer] = {
    APPSTREAMDUMPS.get(
      s"IMAGE_${image}",
      js.Dynamic.literal(
        "type" -> "arrayBuffer",
      )    
    ).toFuture
  }
}
