package filters

import _root_.pretty.please
import play.mvc.Http.Request
import org.apache.commons.lang.StringUtils
import java.util.Date
import java.text.SimpleDateFormat
import pretty.please._

/**
 * Search Filters
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class SearchFilters(var ne: Option[Double], var sw: Option[Double], var nw: Option[Double], var se: Option[Double], var geohashes: Option[List[String]] = None, var zoom: Option[Int] = None) {

    /**
     * Log Debug
     */
    please log "NE: " + ne
    please log "SW: " + sw
    please log "NW: " + nw
    please log "SE: " + se
    please log "Geohashes: " + geohashes
    please log "Zoom: " + zoom

    /**
     * Format Date
     */
    def format(date: Date) = dateFormat format date

    /**
     * Geohash Precision
     */
    def geohashPrecision: Int = zoom match {
        case Some(z: Int) if z > 0 => 22 - z
        case _ => 1
    }

    /**
     * Geohash Suffix
     */
    def geohashSuffix: String = geohashPrecision toString

    /**
     * Check
     */
    def check(param: Option[List[String]], value: String)(implicit request:play.api.mvc.RequestHeader) = {
        please log "Checkbox - List: " + param + ", Value: " + value
        please log "Method: " + request.method + ", Query String: " + request.queryString
        if (request.method == "GET" && request.queryString.isEmpty) {
            "checked"
        } else {
            val list = param.getOrElse(List[String]()).filter(_ == value).map(c => "checked")
            if (list.isEmpty) {
                ""
            } else {
                list.head
            }
        }
    }

    /**
     * Has Bounds
     */
    def hasBounds: Boolean = {
        please log "Has Bounds? NE: " + ne + ", SW: " + sw + ", NW: " + nw + ", SE: " + se
        if (ne.valid && nw.valid && nw.valid && se.valid) {
            please log "Yes!"
            true
        } else {
            please log "No!"
            false
        }
    }

    /**
     * To Query String
     */
    def toQueryString = {
        // Start
        val sb = new StringBuilder

        // Map Bounds
        if (hasBounds) {
            sb append "ne=" append ne
            sb.append("&")
            sb append "sw=" append sw
            sb.append("&")
            sb append "nw=" append nw
            sb.append("&")
            sb append "se=" append se
            sb.append("&")
        }

        // Zoom
        zoom match {
            case Some(s) => sb.append("zoom=").append(s).append("&")
            case _ => please log "No zoom level defined!"
        }

        // Log Debug
        please log "Query String: " + sb.toString
        sb.toString
    }

}