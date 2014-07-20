package domain

import scala.collection.mutable.ListBuffer
import org.pegdown.PegDownProcessor
import java.util.regex.Pattern
import play.api.Logger
import org.pegdown.plugins.PegDownPlugins
import org.markdown4j.Markdown4jProcessor

/**
 * Created by ed on 7/17/14.
 */
class FormattableContent(val title: String, val body: String) {
  /**
   * Posts are markdown, but have embedded endnotes/sidebar content (similar to some WP addons).
   */
  def generate(articleIndex: Int, fullPost: Boolean): (String, Seq[String]) = {
    val buffer = new StringBuilder(body)

    val notes: ListBuffer[(Int, String, Int, Int)] = ListBuffer[(Int, String, Int, Int)]()
    val matcher = FormattableContent.ENDNOTE_REGEX.matcher(buffer)
    val markdown = FormattableContent.processor
    var idx = 0
    while (matcher.find()) {
      idx += 1
      // grabs the inner capture group, but recording the outside ones for replacement.
      notes += ((idx, markdown.process(matcher.group(1)), matcher.start(0), matcher.end(0)))
    }

    if (fullPost) {
      FormattableContent.handleBreaks(buffer)
      notes.reverseIterator.foreach(t => {
        val anchorTarget = s"a${articleIndex}e${t._1}"
        buffer.replace(t._3, t._4, s"<a id='${anchorTarget}-a' href='#${anchorTarget}' class='sidenote-anchor'>${t._1}</a>")
      })

      (markdown.process(buffer.toString()),
                notes.map(t => markdown.process(t._2).replace("<p>", "").replace("</p>", "")).toSeq)
    } else {
      notes.reverseIterator.foreach(t => {
        // TODO: create a fake style that mimics the endnote style, but isn't clickable.
        buffer.replace(t._3, t._4, s"<a class='sidenote-anchor'>${t._1}</a>")
      })

      (FormattableContent.BREAK_REGEX.split(markdown.process(buffer.toString), 2)(0), Seq())
    }
  }
}

private object FormattableContent {
  private lazy val ENDNOTE_REGEX = Pattern.compile(""" \[ref\](.*?)\[/ref\] """.trim)
  private lazy val BREAK_REGEX = Pattern.compile(""" \<\!-- break --\> """.trim)
  private lazy val CODE_REGEX = Pattern.compile(""" ^```([A-Za-z0-9\-\_\.]|[^\n]+?)\n([\n.]*?)```$ """.trim, Pattern.MULTILINE)

  private def processor = {
    new Markdown4jProcessor()
  }

  private def handleBreaks(buffer: StringBuilder): StringBuilder = {
    buffer.replaceAllLiterally("<!-- break -->", "<a id='break'></a>")

    buffer
  }
}