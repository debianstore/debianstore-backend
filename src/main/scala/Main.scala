import app.APPSTREAMDUMPS
import app.ApiClient
import app.EnvVars
import app.FullItemInfo
import app.GhClient
import app.KvClient
import app.ShortItemInfo
import app.StringExtensions.substringAfterFirst
import app.StringExtensions.substringAfterLast
import app.StringExtensions.substringBeforeFirst
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.Request
import org.scalajs.dom.Response
import org.scalajs.dom.ResponseInit
import org.scalajs.dom.experimental.serviceworkers.FetchEvent

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.URIUtils
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import scalajs.js.JSConverters.JSRichFutureNonThenable
import scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce
implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

object Main {

  val okPlainInit = new ResponseInit {
    status = 200
    headers = js.Dictionary(
      "Content-Type" -> "text/plain",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }

  val okPngInit = new ResponseInit {
    status = 200
    headers = js.Dictionary(
      "Content-Type" -> "image/png",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }  

  val notFoundPlainInit = new ResponseInit {
    status = 404
    headers = js.Dictionary(
      "Content-Type" -> "text/plain",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }

  val okJsonInit = new ResponseInit {
    status = 200
    headers = js.Dictionary(
      "Content-Type" -> "application/json",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }

  val errPlainInit = new ResponseInit {
    status = 500
    headers = js.Dictionary(
      "Content-Type" -> "text/plain",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }

  val errJsonInit = new ResponseInit {
    status = 500
    headers = js.Dictionary(
      "Content-Type" -> "application/json",
      "Access-Control-Allow-Origin" -> EnvVars.ACCESS_CONTROL_ALLOW_ORIGIN
    )
  }

  def main(args: Array[String]): Unit = {
    Globals.addEventListener("fetch", (event: FetchEvent) => {
      val respFuture = handleRequest(event.request)
        .transform {
          case Failure(exception) => 
            // hide extra details
            Success(new Response(exception.toString, errPlainInit))
          case success =>
            success
        }
      event.respondWith(respFuture.toJSPromise)
    })
  }

  def handleRequest(request: Request): Future[Response] = {
    try {
      val noSchemaUrl = urlWithNoSchema(request.url)
      val contextAndQuery = noSchemaUrl.substringAfterFirst("/")
      val context = contextAndQuery.substringBeforeFirst("?")
      val query = contextAndQuery.substringAfterFirst("?")

      (request.method, context.split("/")) match {

        // 127.0.0.1:8787/api/v1/apps/category/2DGraphics
        case (HttpMethod.GET, Array("api", "v1", "apps", "category", category)) =>
          GhClient.getByCategory(category)
            .map(str => { ShortItemInfo.updateUrlsInArr(str) })
            .map(str => new Response(str, okJsonInit))

        // 127.0.0.1:8787/icons/by-id/nikwi.desktop
        case (HttpMethod.GET, Array("icons", "by-id", appId)) =>
          GhClient.getDetails(appId)
            .map(str => {
              val details = JSON.parse(str)
              FullItemInfo.fillIcons(details)
              val url = details.selectDynamic("iconDesktopUrl").asInstanceOf[String]
              Response.redirect(url, 302)
            })
            
        // 127.0.0.1:8787/icons/by-file/nikwi_nikwi.png
        case (HttpMethod.GET, Array("icons", "by-file", imgFile)) =>
          KvClient.getImage(imgFile)
            .map(arrBuf => {
              new Response(
                arrBuf,
                okPngInit
              )
            })

        // 127.0.0.1:8787/api/v1/apps/search/abc%20fds
        case (HttpMethod.GET, Array("api", "v1", "apps", "search", searchStr)) =>
          val decoded = URIUtils.decodeURI(searchStr)
          KvClient.get(KvClient.ALL_PKGS_KEY)
            .map(str => {
              val allPkgs = GhClient.parseAllPkgs(str)
              val jsArr: js.Array[js.Dynamic] = 
                allPkgs
                  .filter(pkg => pkg.name.toLowerCase().contains(searchStr.toLowerCase()))
                  .map(pkg => { pkg.toJsDynamic() })
                  .map(pkg => ShortItemInfo.updateUrlsInJsDynamic(pkg))
                  .toJSArray
              JSON.stringify(jsArr)
            })            
            .map(str => new Response(str, okJsonInit))

        // 127.0.0.1:8787/api/v1/apps/org.videolan.vlc
        case (HttpMethod.GET, Array("api", "v1", "apps", appId)) =>
          GhClient.getDetails(appId)
            .flatMap(str => FullItemInfo.fillScreenshotsAndIcons(str))
            .map(str => new Response(str, okJsonInit))

        // 127.0.0.1:8787/init_image?url=http://localhost:8000/nikwi_nikwi.png
        case (HttpMethod.GET, Array("init_image")) =>
          val loadUrl = query.substringAfterFirst("url=")
          val imgName = query.substringAfterLast("/")
          ApiClient.fetchGetBytes(loadUrl)
            .flatMap(buff => {
              KvClient.putImage(imgName, buff)
            })
            .map(_ => new Response(s"initialized ${imgName}", okPlainInit))

        case (HttpMethod.GET, Array("init_search")) =>
          GhClient.getAllPkgsStr()
          .flatMap(str => {
            KvClient.put(KvClient.ALL_PKGS_KEY, str)
          })
          .map(_ => new Response("initialized", okPlainInit))

        case (HttpMethod.GET, Array("init", appId)) =>
          GhClient.getDetails(appId)
            .flatMap(str => {
              KvClient.put(appId, str)
              .transform {
                case Failure(exception) => 
                  Success(new Response(exception.toString, okPlainInit))
                case Success(value) =>
                  Success(new Response("success", okPlainInit))
              }
            })
          .map(_ => new Response("success", okJsonInit))

        case (HttpMethod.PUT, Array(_, _, _, key, value)) =>
          Future.successful(new Response("put method called", okPlainInit))
        case _ =>
          Future.successful(
            new Response(s"expected GET /key or PUT /key/value. get on: ${request.url}", errPlainInit))
      }
    } catch {
      case e: Exception => 
        val resp = new Response(s"error: ${e.getMessage()}", errPlainInit)
        Future.successful(resp)
      case e => 
        val resp = new Response(s"runtime error: ${e.getMessage()}", errPlainInit)
        Future.successful(resp)
    }

  }

  private def urlWithNoSchema(url: String): String = {
    if (url.startsWith("http://")) {
      return url.replaceFirst("http://", "")
    } else if (url.startsWith("https://")) {
      return url.replaceFirst("https://", "")
    } else {
      throw new Exception(s"Unknown schema found in url ${url}")
    }
  }
}
