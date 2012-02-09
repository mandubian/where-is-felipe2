import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "where-is-felipe2"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "postgresql" % "postgresql" % "9.0-801.jdbc4",
      "net.liftweb" %% "lift-json" % "2.4-M5",
      "net.liftweb" %% "lift-json-ext" % "2.4-M5",
      "com.thoughtworks.paranamer" % "paranamer-maven-plugin" % "2.2.1",
      "org.apache.lucene" % "lucene-spatial" % "2.9.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
        
        resolvers ++= Seq(
			Resolver.url("Typesafe-Snapshots", url("http://repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.ivyStylePatterns)
		)
		
    )

}
