package components

import repositories._
import services._
import services.{DBService => SDB}
//import play.api.db.DB
import scala.slick.driver.{H2Driver, JdbcProfile, MySQLDriver}
import play.api.db.slick._
import org.specs2.mock.Mockito
import services.Profile

//import scala.slick.jdbc.JdbcBackend.DatabaseDef
import play.api.Play.current

trait Default
  extends SlickUserServiceComponent
  with SlickUserRepositoryComponent
  with SDB
  with Profile
{
  override val profile: JdbcProfile = MySQLDriver
  override def database: Database = DB(play.api.Play.current)
  override val userRepository = new SlickUserRepository
  override val users = new SlickUserService

//  val userRepository:UserRepository = new SlickUsers
}

trait AnormSettings
extends AnormUserServiceComponent
with AnormUserRepositoryComponent
{
  override val userRepository = new AnormUserRepository
  override val users = new AnormUserService
}


trait TestEnvironment
  extends SlickUserServiceComponent
  with SlickUserRepositoryComponent
  with SDB
  with Profile
  with Mockito {
  override val profile = mock[JdbcProfile]
  override def database = mock[Database]
  override val userRepository = mock[UserRepository]
  override val users = mock[UserService]
}