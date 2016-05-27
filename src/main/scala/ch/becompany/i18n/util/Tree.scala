package ch.becompany.i18n.util

case class Tree(name: String, children: Seq[Tree] = Seq()) {

  val indent = 2;

  override def toString(): String = list.mkString("\n")

  def list: Seq[String] =
    name +: children.flatMap(_.list).map(" " * indent + _)

}
