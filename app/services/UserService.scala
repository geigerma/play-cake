package services

import repositories._
import entities.User
import scala.concurrent.Future

trait UserServiceComponent {
  this: UserRepositoryComponent =>

  val users:UserService

  trait UserService {

    def getById(id:Long): Future[Option[User]]

    def findByName(name:String): Future[List[User]]

    def getByEmail(email:String): Future[Option[User]]

    def create(name:String,email:String,password:String): Future[Option[Long]]

    def expire(id:Long): Future[Long]

    def findByAge(age:Int): Future[List[User]]

  }
}



trait SlickUserServiceComponent extends UserServiceComponent{
  this: SlickUserRepositoryComponent =>

//  override val userRepository: UserRepository = new SlickUsers

  class SlickUserService extends UserService{

    def getById(id:Long) = userRepository.getById(id)

    def findByName(name:String) = userRepository.findByName(name)

    def getByEmail(email:String) = userRepository.getByEmail(email)

    def create(name:String,email:String,password:String) = userRepository.create(name,email,password)

    def expire(id:Long) = userRepository.expire(id)

    def findByAge(age:Int) = userRepository.findByAge(age)

  }

}

trait AnormUserServiceComponent extends UserServiceComponent{
  this: AnormUserRepositoryComponent =>

//  override val userRepository: UserRepository = new SlickUsers

  class AnormUserService extends UserService{

    def getById(id:Long) = userRepository.getById(id)

    def findByName(name:String) = userRepository.findByName(name)

    def getByEmail(email:String) = userRepository.getByEmail(email)

    def create(name:String,email:String,password:String) = userRepository.create(name,email,password)

    def expire(id:Long) = userRepository.expire(id)

    def findByAge(age:Int) = userRepository.findByAge(age)

  }

}
