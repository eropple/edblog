package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.json.Json
import play.api.cache.{EhCachePlugin, Cache}
import play.api.Play

/**
 * Created by ed on 7/18/14.
 */
object Admin extends Controller {
  def flushCaches: Action[AnyContent] = Action { req => {
      if (!req.remoteAddress.startsWith("127.0.0.1")) {
        Unauthorized(Json.obj("error" -> true, "message" -> "Unauthorized action."))
      }

      for (p <- Play.current.plugin[EhCachePlugin]) p.manager.clearAll()

      Ok(Json.obj("success" -> true))
    }
  }
}
