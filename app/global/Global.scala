package global

import play.api._
import play.api.db.DB
import domain.data.EdblogSqueryl._
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.{MySQLAdapter, H2Adapter}
import org.squeryl.internals.DatabaseAdapter
import domain.data.Data
import java.io.{PrintWriter, FileWriter}
import scala.Some

/**
 * Created by ed on 7/17/14.
 */
object Global extends GlobalSettings {
  override def onStart(app: Application) {
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("com.mysql.jdbc.Driver") => Some(() => getSession(new MySQLAdapter, app))
      case _ => sys.error("Database driver must be either org.h2.Driver or com.mysql.jdbc.Driver")
    }

    if ({
      val property = System.getProperty("edblog.print_ddl")
      property != null && property.charAt(0).toUpper == 'Y'
    }) {
      Logger.info(s"Printing DDL to stdout.")
      transaction {
        Data.printDdl
      }
    }
  }
  def getSession(adapter:DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)
}