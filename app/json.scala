package PlayParameterReader

import scala.collection.JavaConversions._

import java.lang.reflect.Constructor
import java.sql.Timestamp
import java.text.SimpleDateFormat

//import play.classloading.enhancers.LocalvariablesNamesEnhancer

import net.liftweb.json._
import net.liftweb.json.JsonAST
import net.liftweb.json.JsonDSL._

/**
 * Play Parameter Name Reader
 *
 * @author Felipe Oliveira [@_felipera]
 */
//object PlayParameterNameReader extends ParameterNameReader {
//    def lookupParameterNames(constructor: Constructor[_]) = LocalvariablesNamesEnhancer.lookupParameterNames(constructor)
//}

/**
 * Formats
 *
 * @author Felipe Oliveira [@_felipera]
 */
//object Formats {
//    implicit val formats = new DefaultFormats {
//        override val parameterNameReader = PlayParameterNameReader
//    }
//}
