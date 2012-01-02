package models

import dtos._
import play.data.validation.Annotations._
import org.apache.commons.lang.builder.ToStringBuilder
import play.mvc.Controller
import scala.collection.mutable.ListBuffer
import play.Logger
import java.lang.StringBuffer
import pretty.please
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._
import play.db.anorm._
import play.db.anorm.defaults._
import play.db.anorm
import play.db.anorm.SqlParser._
import org.apache.commons.lang.StringUtils
import play.data.validation.Required
import play.Logger
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._
import play.db.anorm.Id
import pretty.please._
import _root_.pretty.Clockman
import filters._
import java.util.{ Date }
import java.sql.{ Timestamp, ResultSet }

/**
 * To String Trait
 *
 * @author Felipe Oliveira [@_felipera]
 */
trait ToString {
    self: Entity =>

    /**
     * Reflection-Based ToString
     */
    override def toString = ToStringBuilder.reflectionToString(this)

}

/**
 * Entity Base Class
 *
 * @author Felipe Oliveira [@_felipera]
 */
trait Entity extends ToString

/**
 * Site Model
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class Site(
        var id: Pk[Long],
        @Required var name: Option[String],
        @Required var address: Option[String],
        @Required var city: Option[String],
        @Required var state: Option[String],
        @Required var zipcode: Option[String],
        @Required var county: Option[String],
        @Required var latitude: Option[Double],
        @Required var longitude: Option[Double]) extends Entity {

    /**
     * Constructor
     */
    def this(address: Option[String], city: Option[String], state: Option[String], zipcode: Option[String], county: Option[String], latitude: Option[Double], longitude: Option[Double]) {
        this(NotAssigned, None, address, city, state, zipcode, county, latitude, longitude)
    }

}

/**
 * Site Companion Object
 *
 * @author Felipe Oliveira [@_felipera]
 */
object Site extends Magic[Site] {

    /**
     * Count
     */
    def count(implicit filters: SearchFilters): Long = statement("select count(1) as count from Site").as(scalar[Long])

    /**
     * Map Overlay
     */
    def mapOverlay(implicit filters: SearchFilters): MapOverlay = {
        // Get Map Clusters
        val clusters = mapClusters

        // Get Markers
        filters.geohashes = Option(clusters.filter(_.count == 1).map(_.geohash).toList)
        val markers = mapMarkers

        // Define Map Overlay
        new MapOverlay(markers, clusters.filter(_.count > 1))
    }

    /**
     * Map Clusters
     */
    def mapClusters(implicit filters: SearchFilters): List[MapCluster] = {
        // Get Query
        val query = statement("select " + geohashExpression + " as geohash, count(1) as count from Site", "group by " + geohashExpression + " order by count desc")

        // Get Results
        val list: List[MapCluster] = query().filter(_.data(0) != null).map {
            row =>
                {
                    // Get Fields
                    val fields = row.data

                    // Geohash
                    Option(fields(0)) match {
                        case Some(geohash: String) => {
                            // Count
                            val count: Long = fields(1).toString.toLong

                            // Map Cluster
                            MapCluster(geohash, count)
                        }
                        case _ => null
                    }
                }
        } toList

        // Log Debug
        please log "Map Clusters: " + list.size

        // Return List
        list.filter(_ != null)
    }

    /**
     * Map Markers
     */
    def mapMarkers(implicit filters: SearchFilters): List[MapMarker] = {
        // Get Query
        val query = statement("select site.* from Site site", "order by id")

        // Get Results
        val list: List[MapMarker] = query().map {
            row =>
                try {
                    // Id
                    val id = row[String]("id")

                    // Fields
                    val address = row[Option[String]]("address")
                    val city = row[Option[String]]("city")
                    val state = row[Option[String]]("state")
                    val zip = row[Option[String]]("zip")
                    val county = row[Option[String]]("county")
                    val latitude = row[Option[Double]]("latitude")
                    val longitude = row[Option[Double]]("longitude")

                    // Map Marker (coord required)
                    (latitude, longitude) match {
                        case (lat: Some[Double], lng: Some[Double]) => new MapMarker(id, lat.get, lng.get, address, city, state, zip, county)
                        case _ => null
                    }

                } catch {
                    case error: Throwable => {
                        please report error
                        null
                    }
                }
        } toList

        // Log Debug
        please log "Map Markers: " + list.size

        // Return List
        list.filter(_ != null).take(1000)
    }

    /**
     * Statement
     */
    def statement(prefix: String, suffix: Option[String] = None)(implicit filterBy: SearchFilters) = {
        // Params
        val geohashes = filterBy geohashes
        val zoom = filterBy zoom
        val geohashPrecision = filterBy geohashPrecision

        // Params will contain the list of name/value pairs that need to be bound to the query
        val params = new HashMap[String, Any]

        // This is gonna define the statement that we'll use on the find method
        val terms = new StringBuffer
        terms.append(prefix)
        terms.append(" where ")

        // Boundary
        filterBy.hasBounds match {
            case true => {
                params += "nw" -> filterBy.nw.get
                params += "ne" -> filterBy.ne.get
                params += "se" -> filterBy.se.get
                params += "sw" -> filterBy.sw.get
                terms.append("latitude between {nw} and {ne} and longitude between {sw} and {se} and ")
            }
            case _ => please log "Ignoring Map Bounds!"
        }

        // Geohashes
        geohashes match {
            case Some(list) => {
                if (!list.isEmpty) {
                    val values = list.map(_.substring(0, geohashPrecision)).toSet
                    terms.append(geohashExpression + " in (" + multiValues(values) + ") and ")
                    terms.append("geohash is not null and ")
                }
            }
            case _ => please log "Not including geohashes in query!"
        }

        // Final one just in case
        terms.append("1 = {someNumber} ")

        // Suffix
        terms.append(suffix.getOrElse(""))

        // Define SQL
        val sql = terms.toString.trim

        // Define Query
        var query = SQL(sql).on("someNumber" -> 1)
        for (param <- params) {
            please log "Bind - " + param._1 + ": " + param._2
            query = query.on(param._1 -> param._2)
        }

        // Return Query
        query
    }

    /**
     * Geohash Expression
     */
    def geohashExpression(implicit filterBy: SearchFilters): String = filterBy.zoom match {
        case Some(z: Int) if z > 0 => "substring(geohash from 1 for " + filterBy.geohashPrecision.toString + ")"
        case _ => "geohash"
    }
}