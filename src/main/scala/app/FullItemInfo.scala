package app

import app.StringExtensions.substringAfterLast
import app.deb.DebScreenshotClient

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON

import scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce

object FullItemInfo {

  def fillScreenshotsAndIcons(fullItemInfo: String): Future[String] = {
    val info = JSON.parse(fullItemInfo).asInstanceOf[js.Dynamic]
    fillIcons(info)
    fillScreenshots(info)
      .map(dyn => JSON.stringify(dyn))
  }

  def fillIcons(obj: js.Dynamic): js.Dynamic = {
    val prevDesktop = obj.iconDesktopUrl.asInstanceOf[String]
    val prevMobile = obj.iconMobileUrl.asInstanceOf[String]
    if (prevDesktop != null && prevDesktop.startsWith("http://localhost:7777")) {
      val imgFile = prevDesktop.substringAfterLast("/")
      obj.iconDesktopUrl = s"${EnvVars.EXTERNAL_BASE_URL}/icons/by-file/${imgFile}"
    }
    if (prevMobile != null && prevMobile.startsWith("http://localhost:7777")) {
      val imgFile = prevMobile.substringAfterLast("/")
      obj.iconMobileUrl = s"${EnvVars.EXTERNAL_BASE_URL}/icons/by-file/${imgFile}"
    }
    obj
  }

  private def fillScreenshots(obj: js.Dynamic): Future[js.Dynamic] = {
    val prevScreenshots = obj.screenshots.asInstanceOf[js.Array[js.Dynamic]]
    if (!prevScreenshots.isEmpty) {
      return Future.successful(obj)
    }
    DebScreenshotClient.getDebScreenshots(obj.downloadFlatpakRefUrl.asInstanceOf[String])
      .map(screenshots => {
        val newArr = 
          screenshots
            .map(scr => {
              js.Dynamic.literal(
                "thumbUrl" -> scr.thumbImageURL,
                "imgMobileUrl" -> scr.largeImageURL,
                "imgDesktopUrl" -> scr.largeImageURL,
              )            
            })
            .toJSArray
        obj.screenshots = newArr
        obj
      })
  }
}
