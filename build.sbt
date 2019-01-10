
name := "graphqlErrorhandling"

version := "0.1"

scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(
  PlayScala
)

resolvers += "Atlassian Maven Repository" at "https://maven.atlassian.com/content/repositories/atlassian-public/"

libraryDependencies ++= Seq(
  guice,
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.sangria-graphql" %% "sangria" % "1.4.1"
)