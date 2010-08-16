import sbt._

class HPropsProject(info: ProjectInfo) extends DefaultProject(info) {    
  val snapshots = "scala-tools snapshots" at "http://www.scala-tools.org/repo-snapshots"
  val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"
  val metascala = "metascala" %% "metascala" % "0.1"  
  val scalazCore = "com.googlecode.scalaz" %% "scalaz-core" % "5.0"
}
