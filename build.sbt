import net.litola.SassPlugin

name := "edblog"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  // play blahblahblah
  jdbc,
  anorm,
  cache,
  // markdown processor for posts
  "org.pegdown" % "pegdown" % "1.4.2",
  "org.commonjava.googlecode.markdown4j" % "markdown4j" % "2.2-cj-1.0",
  // LRUMap is friend of programmer.
  "com.twitter" %% "util-collection" % "6.18.0",
  "org.apache.commons" % "commons-collections4" % "4.0",
  // databases!
  "org.squeryl" % "squeryl_2.10" % "0.9.6-RC3",
  "mysql" % "mysql-connector-java" % "5.1.10",
  "com.h2database" % "h2" % "1.4.180"
)     

play.Project.playScalaSettings ++ SassPlugin.sassSettings ++ Seq(SassPlugin.sassOptions := Seq("--compass", "-r", "compass"))
