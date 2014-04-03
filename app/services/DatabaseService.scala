package services

//import play.api.db.DB
import play.api.Play.current
import play.api.db.slick.{Database, DB}
import scala.slick.driver.JdbcDriver


//import scala.slick.jdbc.JdbcBackend.DatabaseDef

trait Profile {

  import scala.slick.driver.JdbcProfile

  val profile: JdbcProfile
}

trait DBService {
//  this: DBService with Profile =>
//  import profile.simple._
//  val database: profile.backend.DatabaseDef
  def database: Database  /*Database.forDataSource(DB.getDataSource())*/
}