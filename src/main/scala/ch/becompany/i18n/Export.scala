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

object Export extends App {
  
  import ResourceBundles._
  
  def formatDate(d: Calendar) =
    new SimpleDateFormat("yyyy-MM-dd-HHmm").format(d.getTime)
  
  def spreadsheet(name: String, bundles: Map[ResourceBundle, Map[String, Map[Locale, String]]]) = {
    val locales = bundles.keys.map(_.locales).flatten
    
    val headers = "Key" :: locales.map(_.toLanguageTag.toUpperCase).toList
    val columns = headers.zipWithIndex.map { case (header, i) =>
      Column(index = i, autoSized = true)
    }
    
    val headerStyle = CellStyle(font = Font(bold = true))
    val headerRow = Row(style = headerStyle).withCellValues(headers)

    val missingValueStyle = CellStyle(fillForegroundColor = Color.LightCoral, fillPattern = CellFill.Solid)
    def cell(value: Option[String]) = value match {
      case Some(s) => Cell("[" + s + "]")
      case None => Cell(style = missingValueStyle, value = "")
    }
    
    val rows = bundles flatMap { case (bundle, values) => {
      values map { case (key, localeValues) => {
        Row().withCells(Cell(key) :: locales.map(localeValues.get(_)).map(cell).toList)
      }}
    }}
    
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
    val fileName = s"$name.xlsx"
    sheet.saveAsXlsx(fileName)
    println(s"Saved spreadsheet $fileName")
    s"open $fileName" !
    /*
    println(incompleteBundles.mkString("\n"))
    all(path).foreach(bundle => {
      println("=" * 40)
      bundle.resources.foreach(r => {
        println(r.dir + "/" + r.name + "_" + r.locale)
        //println(r.properties.mkString("\n")))
      })
    })
    */
  }

}