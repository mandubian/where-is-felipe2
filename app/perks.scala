package pretty

import play.Logger
import play.mvc.Controller
import org.apache.commons.lang.exception.ExceptionUtils
import net.liftweb.json.Printer._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import java.util.{ Calendar, Date }
import org.joda.time.Months
import java.net.URLEncoder
import java.text.SimpleDateFormat
import org.apache.commons.lang.StringUtils
import play.api.libs.json._
import scala.collection.mutable.ListBuffer

/**
 * Helper Object
 *
 * @author Felipe Oliveira [@_felipera]
 */
object please {

    /**
     * JSON Formats
     */
    //implicit val formats = PlayParameterReader.Formats.formats

    /**
     * Compress
     */
    def compress[A](ls: List[A]): List[A] = {
        ls.foldRight(List[A]()) {
            (h, r) =>
                if (r.isEmpty || r.head != h) {
                    h :: r
                } else {
                    r
                }
        }
    }

    /**
     * Log
     */
    def log(any: String) = Logger info any

    /**
     * Report Exception
     */
    def report(error: Throwable) = Logger error ExceptionUtils.getStackTrace(error)

    /**
     * Dummy Controller
     */
    private val _dummy = new Controller {}

    /**
     * Conf
     */
    def conf(name: String) = play.api.Play.configuration.getString(name)

    /**
     * Multi Values
     */
    def multiValues(values: Iterable[String]) = values.mkString("'", "','", "'")

    /**
     * URL Encode
     */
    def encode(value: String) = URLEncoder.encode(value, conf("encoding"))

    import _root_.dtos._
    implicit object MapClusterFormat extends Format[MapCluster] {
    	def reads(json: JsValue): MapCluster = MapCluster(
    			(json \ "geohash").as[String],
    			(json \ "count").as[Long],
    			(json \ "latitude").as[Double],
    			(json \ "longitude").as[Double])
    			
		def writes(mc: MapCluster): JsValue = JsObject(List(
				"geohash" -> JsString(mc.geohash),
				"count" -> JsNumber(BigDecimal(mc.count)),
				"latitude" -> JsNumber(BigDecimal(mc.latitude)),
				"longitude" -> JsNumber(BigDecimal(mc.longitude))))
    }
    
    implicit object MapMarkerFormat extends Format[MapMarker] {
    	def reads(json: JsValue): MapMarker = MapMarker(
    			(json \ "id").as[String],
    			(json \ "latitude").as[Double],
    			(json \ "longitude").as[Double],
    			(json \ "address").asOpt[String],
    			(json \ "city").asOpt[String],
    			(json \ "state").asOpt[String],
    			(json \ "zip").asOpt[String],
    			(json \ "county").asOpt[String]
    	)
    			
		def writes(mc: MapMarker): JsValue = {
    			val lb = new ListBuffer[(String, JsValue)]
    			lb ++= List(
    			    "id" -> JsString(mc.id),
    			    "latitude" -> JsNumber(BigDecimal(mc.latitude)),
					"longitude" -> JsNumber(BigDecimal(mc.longitude)))
				if(mc.address.isDefined) lb += "address" -> JsString(mc.address.get)	
				if(mc.city.isDefined) lb += "city" -> JsString(mc.city.get)	
				if(mc.state.isDefined) lb += "state" -> JsString(mc.state.get)	
				if(mc.zip.isDefined) lb += "zip" -> JsString(mc.county.get)	
				if(mc.county.isDefined) lb += "county" -> JsString(mc.county.get)	

				JsObject(lb)
    	}
    }
    
    implicit object MapOverlayFormat extends Format[MapOverlay] {
      def reads(json: JsValue): MapOverlay = MapOverlay(
          (json \ "markers").as[List[MapMarker]],
          (json \ "clusters").as[List[MapCluster]]
      )    
      
      def writes(m: MapOverlay): JsValue = {
    	  JsObject(List(
    			  "markers" -> toJson(m.markers),
    			  "clusters" -> toJson(m.clusters)
    	  ))
      }

    }

    /**
     * Automatically generate a standard perks json response.
     */
//    def jsonify(runnable: => Any):JsValue = {
//        _dummy.Json(pretty(render(decompose(
//            try {
//                val data = runnable match {
//                    // Lift-json has a cow if you pass it a mutable map to render.
//                    case mmapResult: Map[Any, Any] => mmapResult.toMap
//                    case result => result
//                }
//                Map("status" -> 200, "data" -> data)
//            } catch {
//                case error: Throwable =>
//                    please report error
//                    Map("status" -> 409, "errors" -> Map(error.hashCode.toString -> error.getMessage))
//            }
//        ))))
//    	
//    }

    /**
     * Option[Date] to Pimp Date Implicit Conversion
     */
    implicit def date2PimpDate(date: Option[Date]) = new PimpDate(date.getOrElse(new Date))

    /**
     * Option[Date] to Date Implicit Conversion
     */
    implicit def optionDate2Date(date: Option[Date]) = date.getOrElse(new Date)

    /**
     * Int to Pimp Int
     */
    implicit def int2PimpInt(i: Int) = new PimpInt(i)

    /**
     * String to Date
     */
    implicit def stringToDate(string: String): Date = dateFormat parse string

    /**
     * Date to String
     */
    implicit def dateToString(date: Date): String = dateFormat format date

    /**
     * Option Date to String
     */
    implicit def optionDate2String(date: Option[Date]): String = date match {
        case Some(d: Date) => d
        case _ => "n/a"
    }

    /**
     * Option Double to Pimp Option Double
     */
    implicit def optionDoubleToPimpOptionDouble(value: Option[Double]): PimpOptionDouble = new PimpOptionDouble(value)

    /**
     * String to Double
     */
    implicit def stringToDouble(str: Option[String]): Double = {
        str match {
            case Some(s) => {
                try {
                    s.toDouble
                } catch {
                    case ne: NumberFormatException => 0.0
                    case error: Throwable => 0.0
                }
            }
            case _ => 0.0
        }
    }

    /**
     * Date Format
     */
    def dateFormat = new SimpleDateFormat("MM/dd/yyyy")

    /**
     * String to Option String
     */
    implicit def string2OptionString(value: String): Option[String] = Option(value)

    /**
     * Option String to String
     */
    implicit def optionString2String(value: Option[String]): String = value.getOrElse("n/a")

    /**
     * String to Option Double
     */
    implicit def string2OptionDouble(value: String): Option[Double] = try {
        if (StringUtils.isNotBlank(value)) {
            Option(value.toDouble)
        } else {
            None
        }
    } catch {
        case error: Throwable => {
            please report error
            None
        }
    }

    /**
     * Dinossaur Birth Date
     */
    def dinoBirthDate = {
        val c = Calendar.getInstance()
        c.set(0, 0, 0)
        c.getTime
    }

}

/**
 * Pimp Int
 *
 * @author Felipe Oliveira [@_felipera]
 */
case class PimpInt(value: Int) {

    /**
     * Months Ago
     */
    def monthsAgo = {
        val c = Calendar.getInstance()
        c.setTime(new Date)
        c.add(Calendar.MONTH, (value * -1))
        c.getTime
    }

}

/**
 * Pimp Option Double
 *
 * @author Felipe Oliveira [@_felipera
 */
case class PimpOptionDouble(value: Option[Double]) {

    /**
     * Is Valid
     */
    def valid: Boolean = value.getOrElse(0.0) != 0.0

}

/**
 * Pimp Date
 *
 * @author Felipe Oliveira [@_felipera]
 */
class PimpDate(date: Date) {

    /**
     * Is?
     */
    def is = {
        Option(date) match {
            case Some(d) => d
            case _ => new Date
        }
    }

    /**
     * Before?
     */
    def before(other: Date) = date.before(other)

    /**
     * After?
     */
    def after(other: Date) = date.after(other)

    /**
     * Past?
     */
    def past = date.before(new Date)

    /**
     * Future?
     */
    def future = date.after(new Date)

    /**
     * Subtract
     */
    def -(months: Int) = {
        val c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.MONTH, (months * -1))
        c.getTime
    }

}

/**
 * Clockman Object
 *
 * @author Felipe Oliveira [@_felipera]
 */
object Clockman {

    /**
     * Pimp Date
     */
    def is(date: Date) = new PimpDate(date)

}