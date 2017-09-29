package controllers

import javax.inject._

import org.apache.commons.codec.binary.Base64.decodeBase64
import play.api._
import util.Blumpum
import play.api.mvc._
import views.html.defaultpages.unauthorized

object HomeController {
  val COOKIE_KEY_AUTH = "info"
}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
//  def index() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.index())
//  }

def index = Action { request =>
  Ok(views.html.index("Pinboard!"))
}

def pinboard = Action { request =>
  val authInCookie = request.cookies.get(HomeController.COOKIE_KEY_AUTH)
  var authText = ""
  var authOk = true
  println(s"auth in cookie:$authInCookie")
  if (authInCookie.isEmpty){
    val authInHeader = request.headers.get("Authorization")
    if (authInHeader.isEmpty) {
      // no auth, reject request
      authOk = false
    } else {
      println(s"auth in header")
      authText = authInHeader.get.split(" ")(1)
    }
  } else {
    authText = authInCookie.get.value
  }
  println(s"secure ${authOk}/$authText")
  if (!authOk){
    Unauthorized(unauthorized()).withHeaders(WWW_AUTHENTICATE -> "Basic realm=\"Pinboard credentials\"")
  } else {
    val authInfo = new String(decodeBase64(authText.getBytes)).split(":").toList
    val username = authInfo.head
    val password = authInfo(1)
    var bookmarks = Seq[util.Bookmark]()
    try {
      val tagValue = request.queryString.getOrElse("tag",Seq("")).head
      println(s"about to lookup bookmarks using $username for tag $tagValue")
      bookmarks = Blumpum.getPosts(0, tag = tagValue, username, password).sortBy(_.title)
      Ok(views.html.pinboard("Pinboard!",username, tagValue, bookmarks))
        .withCookies(Cookie(HomeController.COOKIE_KEY_AUTH,authText))
    } catch {
      case ex: Error => {
        println(s"$ex")
        Unauthorized(unauthorized()).withHeaders(WWW_AUTHENTICATE -> "Basic realm=\"Pinboard credentials\"")
          .discardingCookies(DiscardingCookie(HomeController.COOKIE_KEY_AUTH))
      }
    }
  }

}

  def logout = Action { request =>
//    Ok(views.html.logout())
//      .discardingCookies(DiscardingCookie(HomeController.COOKIE_KEY_AUTH))
//      .withHeaders((WWW_AUTHENTICATE,""))
    Unauthorized(unauthorized()).withHeaders(WWW_AUTHENTICATE -> "Basic realm=\"Pinboard credentials\"")
      .discardingCookies(DiscardingCookie(HomeController.COOKIE_KEY_AUTH))
  }


}
