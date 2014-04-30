name := """AssignmentSystem"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
    javaCore,
    "org.webjars"         %%      "webjars-play"      %       "2.2.0",
    "org.webjars"         %       "jquery"            %       "2.1.0-3",
    "org.webjars"         %       "bootstrap"         %       "3.1.1-1"
)

play.Project.playScalaSettings
