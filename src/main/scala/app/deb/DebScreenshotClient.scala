package app.deb

import app.ApiClient

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON
implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

object DebScreenshotClient {
  def getDebScreenshots(pkgName: String): Future[List[DebScreenshot]] = {
    ApiClient.fetchGet(
      s"https://screenshots.debian.net/json/package/${pkgName}"
    )
    .map(str => {
      val parsed = JSON.parse(str)
      val arr = parsed.selectDynamic("screenshots").asInstanceOf[js.Array[js.Dynamic]]
      arr
        .map(obj => DebScreenshot(
          obj.thumb_image_url.asInstanceOf[String],
          obj.small_image_url.asInstanceOf[String],
          obj.large_image_url.asInstanceOf[String],
          obj.version.asInstanceOf[String]
        ))
        .toList
    })
  }
}
