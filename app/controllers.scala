package controllers

import models._
import _root_.pretty.{ Clockman, please }
import filters._
import play._
import play.mvc._
import scala.collection.JavaConversions._
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import please._
import java.util.Date
import pretty.please._

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
    def filters = new SearchFilters(ne, sw, nw, se, zoom = zoom)

    /**
     * Zoom
     */
    def zoom: Option[Int] = Option(params.get("zoom")) match {
        case Some(o: String) => Option(o.toInt)
        case _ => Option(1)
    }

    /**
     * NE Bound
     */
    def ne = boundParam("ne")

    /**
     * SW Bound
     */
    def sw = boundParam("sw")

    /**
     * NW Bound
     */
    def nw = boundParam("nw")

    /**
     * SE Bound
     */
    def se = boundParam("se")

    /**
     * Bound Param
     */
    def boundParam(name: String): String = Option(params.get(name)).getOrElse("")

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
    def index = {
        views.Application.html.index(filters)
    }

}

/**
 * Geo Controller
 *
 * @author Felipe Oliveira [@_felipera]
 */
object Geo extends Controller with controllers.Filters {

    /**
     * Map Overlay
     */
    def mapOverlay = jsonify {
        implicit val searchWith = filters
        Site mapOverlay
    }

}