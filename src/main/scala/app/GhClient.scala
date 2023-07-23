package app

import app.ShortItemInfo.fromJsDynamic

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON

object GhClient {
  def getDetails(appId: String): Future[String] = {
    ApiClient.fetchGet(
      s"https://debianstore.github.io/static/${appId}.json"
    )
  }

  def getByCategory(category: String): Future[String] = {
    ApiClient.fetchGet(
      s"https://debianstore.github.io/static/categories/pkgs_${category}.json"
    )
  }

  def getAllPkgsStr(): Future[String] = {
    ApiClient.fetchGet(
      s"https://debianstore.github.io/static/categories/pkgs_All.json"
    )
  }  

  def getAllPkgs(): Future[List[ShortItemInfo]] = {
    getAllPkgsStr()
    .map(parseAllPkgs(_))
  }

  def parseAllPkgs(str: String): List[ShortItemInfo] = {
    val parsed: js.Array[js.Dynamic] = JSON.parse(str).asInstanceOf[js.Array[js.Dynamic]]
    parsed
      .map(obj => {fromJsDynamic(obj)}).toList
  }
}
