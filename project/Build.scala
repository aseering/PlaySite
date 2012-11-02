import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "MyFirstProject"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "mysql" % "mysql-connector-java" % "5.1.21",
		"javax.mail" % "mail" % "1.4.5",
		"org.scalatest" % "scalatest_2.9.1" % "2.0.M4"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
		// Add your own project settings here      
    )

}
