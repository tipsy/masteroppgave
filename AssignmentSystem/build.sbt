name := """AssignmentSystem"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.10.4"

includeFilter in (Assets, LessKeys.less) := "main.less"

libraryDependencies ++= Seq(
    javaCore,
    "org.webjars"           %%          "webjars-play"          %       "2.3.0",
    "org.webjars"           %           "jquery"                %       "2.1.0-3",
    "org.webjars"           %           "bootstrap"             %       "3.1.1-1",
    "org.webjars"           %           "font-awesome"          %       "4.0.3",
    "org.webjars"           %           "ace"                   %       "07.31.2013",
    "org.webjars"           %           "intercooler-js"        %       "0.3.0",
    "org.webjars"           %           "jquery-ui"             %       "1.10.4"
)


