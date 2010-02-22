import sbt._

class ScappsProject(info: ProjectInfo) extends DefaultWebProject(info) {    
  val snapshots = "scala-tools snapshots" at "http://www.scala-tools.org/repo-snapshots"
  val scalatest = "org.scalatest" % "scalatest" % "1.0.1-for-scala-2.8.0.Beta1-RC7-with-test-interfaces-0.3-SNAPSHOT" % "test"
  
  val metascala = "metascala" %% "metascala" % "0.1"
  
  val scalaz_core = "com.googlecode.scalaz" % "scalaz-core_2.8.0.Beta1" % "5.0-SNAPSHOT"
  val scalaz_http = "com.googlecode.scalaz" % "scalaz-http_2.8.0.Beta1" % "5.0-SNAPSHOT"
  
  val belt = "prohax" %% "belt" % "0.1"
}
