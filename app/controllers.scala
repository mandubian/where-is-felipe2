package controllers

import models._
import _root_.pretty.{ Clockman, please }
import filters._
import play._
import scala.collection.JavaConversions._
import please._
import java.util.Date
import pretty.please._
import play.api.mvc._

/**
 * Filters Trait
 *
 * @author Felipe Oliveira [@_felipera]
 */
trait Filters {
    self: Controller =>

    /**
     * Search Filters
     */
    def filters(implicit request:RequestHeader) = new SearchFilters(ne, sw, nw, se, zoom = zoom)

    /**
     * Zoom
     */
    def zoom(implicit request:RequestHeader): Option[Int] = { 
    	request.queryString.get("zoom").flatMap(_.headOption) match {
	        case Some(o: String) => Option(o.toInt)
	        case _ => Option(1)
    	}
    }

    /**
     * NE Bound
     */
    def ne(implicit request:RequestHeader) = boundParam("ne")

    /**
     * SW Bound
     */
    def sw(implicit request:RequestHeader) = boundParam("sw")

    /**
     * NW Bound
     */
    def nw(implicit request:RequestHeader) = boundParam("nw")

    /**
     * SE Bound
     */
    def se(implicit request:RequestHeader) = boundParam("se")

    /**
     * Bound Param
     */
    def boundParam(name: String)(implicit request:RequestHeader): String = request.queryString.get(name).flatMap(_.headOption).getOrElse("")

}

/**
 * Main Controller
 *
 * @author Felipe Oliveira [@_felipera]
 */
object Application extends Controller with controllers.Filters {

    /**
     * Index Action
     */
    def index = Action { implicit request =>
        Ok(views.html.Application.index(filters))
    }

}

/**
 * Geo Controller
 *
 * @author Felipe Oliveira [@_felipera]
 */
object Geo extends Controller with controllers.Filters {
	import play.api.libs.json._
    /**
     * Map Overlay
     */
    def mapOverlay = Action { implicit request =>
      Ok(JsObject(List(
          "result" -> JsNumber(200), 
          "data" -> toJson {    
	        implicit val searchWith = filters
	        Site mapOverlay
          }
      )).as[JsValue])
    }
}