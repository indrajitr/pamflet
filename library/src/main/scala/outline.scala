package pamflet
import com.tristanhunt.knockoff._
import PamfletDiscounter.headerText

object Outline {
  def apply(blocks: Seq[Block]) =
    <ul> {
      build(blocks.view.collect { case h: Header => h }, 0).nodes
    } </ul>
  private case class Return(nodes: xml.NodeSeq, rest: Seq[Header])
  private def build(blocks: Seq[Header], cur: Int): Return =
    blocks match {
      case Seq(a, b, tail @_*) if a.level == cur && b.level > cur =>
        val nested = build(b +: tail, b.level)
        val after = build(nested.rest, cur)
        val name = headerText(a.spans)
        Return((
          <li> <a href={BlockNames.fragment(name)}>{ name }</a>
            <ul> { nested.nodes } </ul>
          </li>
        ) ++ after.nodes, after.rest)
      case Seq(a, tail @ _*) if a.level > cur =>
        val Return(nodes, rest) = build(blocks, a.level)
        Return(nodes, rest)
      case Seq(a, tail @ _*) if a.level == cur =>
        val Return(nodes, rest) = build(tail, cur)
        val name = headerText(a.spans)
        Return(( <li> <a href={BlockNames.fragment(name)}>
                { name } </a> </li> ) ++ nodes, rest)
      case _ =>
        Return(Seq.empty, blocks)
    }
}