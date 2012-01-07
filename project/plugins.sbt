resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns),
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.url("Play@mandubian", url("http://ci.mandubian.com/ivy-releases/"))(Resolver.ivyStylePatterns)
)

addSbtPlugin("play" % "sbt-plugin" % "2.0-RC1-SNAPSHOT")
