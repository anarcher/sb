import sbt._

class sbProject(info: ProjectInfo) extends DefaultProject(info)
{
  override def mainClass = Some("sb")
}
