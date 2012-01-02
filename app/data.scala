package dtos

import scala.collection.JavaConversions._
import scala.collection.JavaConversions

/**
 * Map Marker
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class MapMarker(id: String, latitude: Double, longitude: Double, address: Option[String], city: Option[String], state: Option[String], zip: Option[String], county: Option[String])

/**
 * Map Cluster
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class MapCluster(geohash: String, count: Long, latitude: Double, longitude: Double)

/**
 * Map Cluster Companion
 *
 * @author Felipe Oliveira [@_felipera]
 */
object MapCluster {

    /**
     * Constructor
     */
    def apply(geohash: String, count: Long): MapCluster = {
        val coords = org.apache.lucene.spatial.geohash.GeoHashUtils.decode(geohash)
        new MapCluster(geohash, count, coords(0), coords(1))
    }

}

/**
 * Map Overlay
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class MapOverlay(markers: List[MapMarker], clusters: List[MapCluster])

