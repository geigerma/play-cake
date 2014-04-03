package repositories

import entities._
import scala.language.postfixOps
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

import anorm.SQL
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.JdbcDriver
import scala.slick.jdbc.JdbcBackend.DatabaseDef

trait UserRepositoryComponent {

  val userRepository: UserRepository

  trait UserRepository {

    def getById(id: Long): Future[Option[User]]

    def findByName(name: String): Future[List[User]]

    def getByEmail(email: String): Future[Option[User]]

    def create(name: String, email: String, password: String): Future[Option[Long]]

    def expire(id: Long): Future[Long]

    def findByAge(age: Int): Future[List[User]]

  }

}

trait AnormUserRepositoryComponent extends UserRepositoryComponent {

  class AnormUserRepository extends UserRepository {

    def getById(id: Long) = Future {
      DB.withConnection {
        implicit connection =>
          SQL(
            """
            SELECT
              id,
              name,
              email,
              password,
              salt,
              active,
              age
            FROM user
            WHERE id = {id}
            LIMIT 1
            """
          ).on(
              'id -> id
            ).as(User.fromDB.singleOpt)
      }
    }

    def findByName(name: String) = Future {
      DB.withConnection {
        implicit connection =>
          SQL(
            """
            SELECT
              id,
              name,
              email,
              password,
              salt,
              active,
              age
            FROM user
            WHERE name LIKE '%' || {name} || '%'
            """
          ).on(
              'name -> name
            ).as(User.fromDB *)
      }
    }

    def findByAge(age: Int) = Future {
      DB.withConnection {
        implicit connection =>
          SQL(
            """
          SELECT
            id,
            name,
            email,
            password,
            salt,
            active,
            age
          FROM user
          WHERE age={age}
            """).on(
              'age -> age
            ).as(User.fromDB *)
      }
    }

    def getByEmail(email: String) = Future {
      DB.withConnection {
        implicit connection =>
          SQL(
            """
            SELECT
              id,
              name,
              email,
              password,
              salt,
              active,
              age
            FROM user
            WHERE email = {email}
            LIMIT 1
            """
          ).on(
              'email -> email
            ).as(User.fromDB.singleOpt)
      }
    }

    def create(name: String, email: String, password: String) = Future {
      val salt = "somesalt"
      DB.withConnection {
        implicit connection =>
          SQL(
            """
            INSERT INTO user (
              name,
              email,
              password,
              salt
            ) VALUES (
              {name},
              {email},
              {password},
              {salt}
            );
            """).on(
              'name -> name,
              'email -> email,
              'password -> password,
              'salt -> salt
            ).executeInsert()
      }
    }

    def expire(id: Long) = Future {
      DB.withConnection {
        implicit connection =>
          SQL(
            """
            UPDATE user
            SET active = 0
            WHERE id = {id}
            """
          ).on(
              'id -> id
            ).executeUpdate()
      }
    }
  }

}

import services.{DBService => SDB}
import services.Profile

trait SlickUserRepositoryComponent extends UserRepositoryComponent {
  this: UserRepositoryComponent with Profile with SDB =>

  import profile.simple._

  //  import scala.slick.driver.MySQLDriver.simple._
  //  import scala.slick.driver.JdbcProfile
  //  import scala.slick.driver.MySQLDriver.simple._
  //  val userRepository = new SlickUsers

  class UsersTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def email = column[String]("email")

    def password = column[String]("password")

    def salt = column[String]("salt")

    def active = column[Boolean]("active")

    def age = column[Int]("age")

    def * = (id, name, email, password, salt, active, age) <>(User.tupled, User.unapply)

  }

  class SlickUserRepository extends UserRepository {


    val users = TableQuery[UsersTable]

    def getById(id: Long): Future[Option[User]] = {
      database.withSession {
        implicit session =>
          Future.successful {
            (for {
              u <- users if u.id === id
            } yield u).firstOption
          }
      }
    }

    def findByName(name: String): Future[List[User]] = {
      database.withSession {
        implicit session =>
          Future.successful {
            (for {
              u <- users if u.name like s"%$name%"
            } yield u).list()
          }
      }
    }

    def getByEmail(email: String): Future[Option[User]] = {
      database.withSession {
        implicit session =>
          Future.successful {
            (for {
              u <- users if u.email === email
            } yield u).firstOption
          }
      }
    }

    def create(name: String, email: String, password: String): Future[Option[Long]] = {
      database.withSession {
        implicit session =>
          Future.successful {
            val user = new User(0, name, email, password, "someSalt", age = 24)
            Option((users returning users.map(_.id)) += user)
          }
      }
    }

    //Not exactly sure what I have this returning
    def expire(id: Long): Future[Long] = {
      database.withSession {
        implicit session =>
          Future.successful {
            (for {
              u <- users if u.id === id
            } yield u.active).update(false)
          }
      }
    }

    def findByAge(age: Int): Future[List[User]] = {
      database.withSession {
        implicit session =>
          Future.successful {
            (for {
              u <- users if u.age === age
            }yield u).list()
          }
      }
    }


  }

}
