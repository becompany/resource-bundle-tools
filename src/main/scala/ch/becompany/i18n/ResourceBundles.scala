package ch.becompany.i18n

import scalax.file.Path
import java.util.Locale
import org.apache.commons.lang.LocaleUtils
import scalax.io.{Resource => IOResource}
import java.io.BufferedReader
import java.io.FileReader
import com.typesafe.config.ConfigFactory
import java.io.File
import scala.collection.JavaConversions._
import java.util.Map.Entry
import com.typesafe.config.ConfigValue

case class Resource(dir: Path, name: String, locale: Locale) {

  private lazy val path = s"${dir.path}/${name}_$locale.properties"
  
  private lazy val config = ConfigFactory.parseFile(new File(path))

  lazy val properties: Map[String, String] =
    config.entrySet.map(entry => (entry.getKey, config.getString(entry.getKey))).toMap

}

object Resource {
  val pattern = "(\\w+)_([a-z\\-]+)\\.properties".r
  def apply(path: Path): Option[Resource] =
    pattern.unapplySeq(path.name) flatMap {
      case List(name, locale) => {
        try {
          Some(Resource(path.parent.get, name, LocaleUtils.toLocale(locale)))
        } catch {
          case e: IllegalArgumentException => None
        }
      }
    }
}

case class ResourceBundle(dir: Path, name: String, resources: Seq[Resource]) {
  
  lazy val locales = resources.map(_.locale).toSet
  
  private lazy val keys = resources.map(_.properties.keys).flatten.toSet
  
  private lazy val locale2resource = resources map (r => r.locale -> r) toMap
  
  private def values(key: String): Map[Locale, String] =
    resources.
      filter(_.properties.contains(key)).
      map(r => (r.locale, r.properties(key))).
      toMap
  
  def incomplete(): Map[String, Map[Locale, String]] =
    keys.map(k => (k, values(k))).
      filter(_._2.size < locales.size).
      toMap

}

object ResourceBundles {
  
  def allPaths(cwd: Path) =
    cwd ** "src" / "main" ** "*_*.properties"
  
  def all(cwd: Path): Iterable[ResourceBundle] =
     allPaths(cwd).toSeq.
       flatMap(Resource(_)).
       groupBy(r => (r.dir, r.name)).
       map { case (key, resources) => ResourceBundle(key._1, key._2, resources) }
  
}