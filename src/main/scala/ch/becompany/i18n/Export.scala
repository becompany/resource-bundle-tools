package ch.becompany.i18n

import java.text.SimpleDateFormat
import java.util.{Calendar, GregorianCalendar, Locale}

import com.norbitltd.spoiwo.model.WidthUnit._
import com.norbitltd.spoiwo.model.enums.CellFill
import com.norbitltd.spoiwo.model.{Cell, Row, Sheet, _}
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._

import scala.sys.process._
import scalax.file.Path

/**
  * Created by nobby on 27.05.16.
  */
object Export {

  import ResourceBundles._

  private val headerStyle = CellStyle(font = Font(bold = true))

  private val keyStyle = CellStyle(font = Font(italic = true))

  private val missingValueStyle = CellStyle(
    fillForegroundColor = Color(255, 230, 230),
    fillPattern = CellFill.Solid
  )

  private def formatDate(d: Calendar) =
    new SimpleDateFormat("yyyy-MM-dd-HHmm").format(d.getTime)

  private def spreadsheet(name: String, bundles: Map[ResourceBundle, Map[String, Map[Locale, String]]]) = {
    val locales = bundles.keys.map(_.locales).flatten.toList

    val headers = "Bundle" :: "Key" :: locales.map(_.toLanguageTag.toUpperCase).toList
    val columns = headers.zipWithIndex.map { case (header, i) =>
      Column(index = i, width = new Width(i match {
        case 0 => 20 // Bundle
        case 1 => 30 // Key
        case _ => 40 // Messages
      }, Character))
    }

    val headerRow = Row(style = headerStyle).withCellValues(headers)

    def cell(value: Option[String]) = value match {
      case Some(s) => Cell(s)
      case None => Cell(style = missingValueStyle, value = "")
    }

    val rows = for {
      (bundle, values) <- bundles.toSeq.sortBy(_._1.name)
      (key, localeValues) <- values.toSeq.sortBy(_._1)
    } yield {
      Row().withCells(
        Cell(bundle.name, style = keyStyle) ::
          Cell(key, style = keyStyle) ::
          locales.map(localeValues.get(_)).map(cell))
    }

    val sheet = Sheet(name = name).
      withColumns(columns).
      withRows(headerRow :: rows.toList)
    sheet
  }

  def export(path: Path): Unit = {
    val date = formatDate(new GregorianCalendar())
    val incompleteBundles = all(path).
      map(bundle => (bundle, bundle.incomplete)).
      filter(!_._2.values.isEmpty).
      toMap

    val name = s"Missing_Translations_$date"
    val sheet = spreadsheet(name, incompleteBundles)
    val fileName = s"target/$name.xlsx"
    sheet.saveAsXlsx(fileName)
    println(s"Saved spreadsheet $fileName")
    s"open $fileName" !
  }
}
