package ch.becompany.i18n

import scalax.file.Path
import scalax.file.ImplicitConversions._
import com.norbitltd.spoiwo.model.Sheet
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Date
import java.util.Calendar
import com.norbitltd.spoiwo.model.CellStyle
import com.norbitltd.spoiwo.model.Font
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import java.util.Locale
import com.norbitltd.spoiwo.model.Row
import com.norbitltd.spoiwo.model.Cell
import com.norbitltd.spoiwo.model.Column
import com.norbitltd.spoiwo.model.Color
import sys.process._
import org.apache.poi.ss.usermodel.FillPatternType
import com.norbitltd.spoiwo.model.enums.CellFill
import com.norbitltd.spoiwo.model.Width
import com.norbitltd.spoiwo.model.WidthUnit._

object Export extends App {

  val headerStyle = CellStyle(font = Font(bold = true))

  val keyStyle = CellStyle(font = Font(italic = true))

  val missingValueStyle = CellStyle(
      fillForegroundColor = Color(255, 230, 230),
      fillPattern = CellFill.Solid
  )

  import ResourceBundles._
  
  def formatDate(d: Calendar) =
    new SimpleDateFormat("yyyy-MM-dd-HHmm").format(d.getTime)
  
  def spreadsheet(name: String, bundles: Map[ResourceBundle, Map[String, Map[Locale, String]]]) = {
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
  
  if (args.size != 1) println("Usage: <path>")
  else {
    val date = formatDate(new GregorianCalendar())
    val path = args(0)
    def incompleteBundles = all(path).
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