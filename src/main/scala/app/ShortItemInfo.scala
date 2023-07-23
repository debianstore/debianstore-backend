package app

import app.StringExtensions.substringAfterFirst
import app.StringExtensions.substringAfterLast

import scala.scalajs.js
import scala.scalajs.js.JSON

object ShortItemInfo {
  val FLATPAK_APP_ID = "flatpakAppId"
  val NAME = "name"
  val SUMMARY = "summary"
  val CURRENT_RELEASE_VERSION = "currentReleaseVersion"
  val CURRENT_RELEASE_DATE = "currentReleaseDate"
  val ICON_DESKTOP_URL = "iconDesktopUrl"
  val ICON_MOBILE_URL = "iconMobileUrl"
  val IN_STORE_SINCE_DATE = "inStoreSinceDate"


  def fromJsDynamic(obj: js.Dynamic): ShortItemInfo = {
    ShortItemInfo(
      obj.selectDynamic(ShortItemInfo.FLATPAK_APP_ID).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.NAME).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.SUMMARY).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.CURRENT_RELEASE_VERSION).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.CURRENT_RELEASE_DATE).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.ICON_DESKTOP_URL).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.ICON_MOBILE_URL).asInstanceOf[String],
      obj.selectDynamic(ShortItemInfo.IN_STORE_SINCE_DATE).asInstanceOf[String],
    )
  }

  def updateUrlsInJsDynamic(obj: js.Dynamic): js.Dynamic = {
    val iconDesktop = obj.selectDynamic(ShortItemInfo.ICON_DESKTOP_URL).asInstanceOf[String]
    val iconMobile = obj.selectDynamic(ShortItemInfo.ICON_MOBILE_URL).asInstanceOf[String]
    if (iconDesktop != null && iconDesktop.startsWith("http://localhost:7777/")) {
      val imgName = iconDesktop.substringAfterLast("/")
      val newIconDesktop = s"${EnvVars.EXTERNAL_BASE_URL}/icons/by-id/${imgName}"
      obj.updateDynamic(ShortItemInfo.ICON_DESKTOP_URL)(newIconDesktop)
    }
    if (iconMobile != null && iconMobile.startsWith("http://localhost:7777/")) {
      val imgName = iconDesktop.substringAfterLast("/")
      val newIconMobile = s"${EnvVars.EXTERNAL_BASE_URL}/icons/by-id/${imgName}"
      obj.updateDynamic(ShortItemInfo.ICON_MOBILE_URL)(newIconMobile)
    }
    obj
  }

  def updateUrlsInArr(str: String): String = {
    val parsed = JSON.parse(str).asInstanceOf[js.Array[js.Dynamic]]
    for (obj <- parsed) {
      updateUrlsInJsDynamic(obj)
    }
    JSON.stringify(parsed)
  }
}

case class ShortItemInfo (
    val flatpakAppId : String,
    val name : String,
    val summary : String,
    val currentReleaseVersion : String,
    val currentReleaseDate : String,
    val iconDesktopUrl : String,
    val iconMobileUrl : String,
    val inStoreSinceDate : String
):
  def toJsDynamic(): js.Dynamic = {
    js.Dynamic.literal(
      ShortItemInfo.FLATPAK_APP_ID -> this.flatpakAppId,
      ShortItemInfo.NAME -> this.name,
      ShortItemInfo.SUMMARY -> this.summary,
      ShortItemInfo.CURRENT_RELEASE_VERSION -> this.currentReleaseVersion,
      ShortItemInfo.CURRENT_RELEASE_DATE -> this.currentReleaseDate,
      ShortItemInfo.ICON_DESKTOP_URL -> this.iconDesktopUrl,
      ShortItemInfo.ICON_MOBILE_URL -> this.iconMobileUrl,
      ShortItemInfo.IN_STORE_SINCE_DATE -> this.inStoreSinceDate                  
    )
  }
