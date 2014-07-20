package domain.data

import org.squeryl.PrimitiveTypeMode
import org.squeryl.dsl._
import java.sql.Timestamp
import org.joda.time.{DateTimeZone, DateTime}

/**
 * Squeryl, as of 0.9.6, wants you to have your own object that extends
 * PrimitiveTypeMode for your helpers, implicits, etc.
 */
object EdblogSqueryl extends PrimitiveTypeMode {
  implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, DateTime, TTimestamp](timestampTEF, this) {
    def convertFromJdbc(t: Timestamp) = new DateTime(t, DateTimeZone.UTC)
    def convertToJdbc(t: DateTime) = new Timestamp(t.toDateTime(DateTimeZone.UTC).getMillis)
  }
  implicit val optionJodaTimeTEF =
    new TypedExpressionFactory[Option[DateTime], TOptionTimestamp]
      with DeOptionizer[Timestamp, DateTime, TTimestamp, Option[DateTime], TOptionTimestamp] {

      val deOptionizer = jodaTimeTEF
    }
  implicit def jodaTimeToTE(s: DateTime) = jodaTimeTEF.create(s)
  implicit def optionJodaTimeToTE(s: Option[DateTime]) = optionJodaTimeTEF.create(s)
}
