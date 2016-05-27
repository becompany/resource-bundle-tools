package ch.becompany.i18n

import scalax.file.ImplicitConversions._

object Command {
  sealed abstract class Command(val name: String)
  object Export extends Command("export")
  object Duplicate extends Command("duplicate")
  val commands = Seq(Export, Duplicate)
  def apply(cmd: String) = commands.find(_.name == cmd)
}

case class Config(
  mode: String = "",
  path: String = "",
  values: Boolean = false
)

object Main extends App {

  val parser = new scopt.OptionParser[Config]("i18n.sh") {
    head("i18n", "1.0.0-SNAPSHOT")
    cmd(Command.Export.name) action { (_, c) =>
      c.copy(mode = Command.Export.name)
    } text("Export missing keys as spreadsheet.")
    cmd(Command.Duplicate.name) action { (_, c) =>
      c.copy(mode = Command.Duplicate.name)
    } text("List duplicate keys.") children(
      opt[Unit]('v', "values") text("Output values") action { (_, c) =>
        c.copy(values = true)
      }
    )
    arg[String]("<path>") text("Path to project") action { (p, c) =>
      c.copy(path = p)
    }
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      Command(config.mode) match {
        case Some(Command.Export) => Export.export(config.path)
        case Some(Command.Duplicate) => Report.duplicates(config.path, config.values)
        case _ => parser.showUsageAsError()
      }
    case None => ()
  }

}