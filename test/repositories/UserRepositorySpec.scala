package test.repositories

import test._

import play.api.test.Helpers._

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.db.slick._
import scala.slick.driver.{H2Driver, JdbcProfile}

class UserRepositorySpec
  extends Specification
  with components.TestEnvironment
  with Mockito {

  override val profile: JdbcProfile = H2Driver
  override val userRepository = new SlickUserRepository

  override def database: Database = DB(play.api.Play.current)

  "UserRepository.getById" should {
    "return some user if it exists" in new WithApp {


      val result = userRepository.getById(1L)

      Await.result(result, Duration(500, MILLISECONDS)) must beSome

    }
  }

  "UserRepository.findByName" should {
    "return a list of users" in new WithApp {

      val timeout = Duration(500, MILLISECONDS)

            Await.result(userRepository.findByName("a"), timeout).length must between(6, 7)
            Await.result(userRepository.findByName("b"), timeout).length must between(1, 1)
            Await.result(userRepository.findByName("c"), timeout).length must between(2, 2)
            Await.result(userRepository.findByName("d"), timeout).length must between(2, 3)
            Await.result(userRepository.findByName("e"), timeout).length must between(5, 5)
            Await.result(userRepository.findByName("f"), timeout).length must between(1, 2)
            Await.result(userRepository.findByName("g"), timeout).length must between(1, 1)
            Await.result(userRepository.findByName("h"), timeout).length must between(1, 2)

    }
  }

    "UserRepository.getByEmail" should {
      "return a user when there is an exact match" in new WithApp {

          val result = userRepository.getByEmail("ben@company.com")

          Await.result(result, Duration(500, MILLISECONDS)) must beSome

      }
    }

    "UserRepository.findByAge" should {
      "return a list of users with the provided age" in new WithApp {
          val timeout = Duration(500, MILLISECONDS)
          Await.result(userRepository.findByAge(26), timeout).length mustEqual(1)
          Await.result(userRepository.findByAge(52), timeout).length mustEqual(1)
          Await.result(userRepository.findByAge(0), timeout).length mustEqual(8)
      }
    }

    "UserRepository.create" should {
      "create a new user" in new WithApp {

          val request = userRepository.create("zed","zed@company.com","zed's Ultimate password!")

          val result = Await.result(request, Duration(500, MILLISECONDS))

          result must beSome

          result.get must beGreaterThan(0L)

      }
    }

    "UserRepository.expire" should {
      "expire the given user" in new WithApp {

          val result = userRepository.expire(1L)

          Await.result(result, Duration(500, MILLISECONDS)) must equalTo(1L)
      }
    }
}
