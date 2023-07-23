package app

import org.scalajs.dom
import org.scalajs.dom.Blob
import org.scalajs.dom.Fetch
import org.scalajs.dom.Headers
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.Request
import org.scalajs.dom.RequestInit

import scala.concurrent.Future
import scala.scalajs.js.typedarray.ArrayBuffer
implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

object ApiClient {

  def fetchGet(url: String): Future[String] = {
    dom
      .fetch(url, new RequestInit { method = HttpMethod.GET })
      .toFuture
      .flatMap(resp => {
        if (resp.status != 200) {
          throw Exception(s"Query failed with status ${resp.statusText}")
        }
        resp.text().toFuture
      })
  }

  def fetchGetBytes(url: String): Future[ArrayBuffer] = {
    dom
      .fetch(url, new RequestInit { method = HttpMethod.GET })
      .toFuture
      .flatMap(resp => {
        if (resp.status != 200) {
          throw Exception(s"Query failed with status ${resp.statusText}")
        }
        resp.arrayBuffer().toFuture
      })
  }
}