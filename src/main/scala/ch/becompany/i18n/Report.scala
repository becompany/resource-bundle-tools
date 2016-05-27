package ch.becompany.i18n

import ch.becompany.i18n.util.Tree

import scala.collection.SortedMap
import scalax.file.Path

object Report {

  import ResourceBundles._

  def duplicates(path: Path, outputValues: Boolean): Unit = {
    val duplicates = all(path).
      flatMap(bundle => bundle.keys.map(key => (key, bundle))).
      toSeq.
      groupBy(_._1).
      toSeq.
      sortBy(_._1).
      filter(_._2.size > 1)

    val trees = duplicates map { case (key, bundles) =>
      val bundleTrees = bundles map { case (key, bundle) =>
        val bundlePath = (bundle.dir / bundle.name).relativize(path).path
        if (outputValues) {
          Tree(bundlePath, bundle.values(key).
            map { case (locale, v) => (locale.getLanguage, v) }.
            toSeq.
            sortBy(_._1).
            map { case (lang, v) => Tree(s"$lang: $v") })
        } else {
          Tree(bundlePath)
        }
      }
      Tree(key, bundleTrees)
    }

    println(trees.mkString("\n"))
    println(s"Found ${trees.size} duplicates.")
  }

}
