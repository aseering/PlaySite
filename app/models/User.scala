package models

import scala.util.Random

import play.api.db._
import play.api.Play.current
import play.api.libs.Crypto

import anorm._
import anorm.SqlParser._

case class User(id: Pk[Long], email: String, password: String, is_superuser: Boolean)

abstract class PasswordComponents
case class SignPasswordComponents(algorithm: String, salt: String, hash: String) extends PasswordComponents

object User {
  
  /**
   * 'Cause the Scala doc told me to.
   */
  val simple = {
	get[Pk[Long]]("users.id") ~
	get[String]("users.email") ~
	get[String]("users.password") ~
	get[Boolean]("users.is_superuser") map {
	  case id~email~password~is_superuser => User(id, email, password, is_superuser)
	}
  }

  /**
   * Return the User object corresponding to the specified user e-mail address
   */
  def findByEmail(email: String) : Option[User] = {
	  DB.withConnection { implicit connection => 
  		SQL("select * from users where email = {email};").on('email -> email).as(User.simple.singleOpt)
	  }
  }
  
  /**
   * Make the specified user into a superuser in the database
   * Does not update existing login sessions
   */
  def makeSuperuser(email: String) {
	  DB.withConnection { implicit connection =>
		SQL("update users set is_superuser = t where email = {email};").on('email -> email).executeUpdate()
	  }
  }
  
  /**
   * Make the specified user no longer a superuser
   * Does not update existing login sessions
   */
  def unMakeSuperuser(email: String) {
	  DB.withConnection { implicit connection =>
		SQL("update users set is_superuser = f where email = {email};").on('email -> email).executeUpdate()
	  }
  }

  /**
   * Add a user to the system.
   * Probably throws some DB exception if the user already exists.
   * TODO:  We should deal with that or something.
   */
  def addUser(email: String, password: String, is_superuser: Boolean = false) {
      DB.withConnection { implicit connection =>
	    SQL("insert into users (email, password, is_superuser) values ({email}, {password}, {is_superuser});").on(
	    		'email -> email,
	    		'password -> encryptPassword(password),
	    		'is_superuser -> is_superuser
	    ).executeInsert()
	  }
  }
  
  /**
   * Remove a user from the system
   * If 'password' is specified, must match or we return false.
   * TODO:  Should deal more intelligently with error conditions.
   */
  def dropUser(email: String, password: Option[String] = None) : Boolean = {
    if (password.isDefined && !login(email, password.get).isDefined) { 
      return false
    }
    if (!findByEmail(email).isDefined) {
      return false
    }
    DB.withConnection { implicit connection => 
      SQL("delete from users where email = {email};").on(
          'email -> email
          ).execute()
    }
    return true
  }
  
  /**
   * Check a password.  Returns true iff the specified (username, password) pair matches something in our DB.
   */
  def login(email: String, password: String) : Option[User] = {
	var userMaybe = findByEmail(email)
	userMaybe match {
	  case Some(user) => {
		  var components = passwordComponents(user.password)
		  if (components != None &&
			  user.password == encryptPassword(password, components.salt)) {
		       return userMaybe
		  } else {
		       return None
		  }
	  }
	  case None => {
		  return None
	  }
	}
  }
  
  
  /**********
   * Utility Functions
   **********/
  
  /**
   * Encrypt a password.  Returns a salted hash of the password 
   */
  def encryptPassword(password: String, salt: String = Random.nextString(10)) : String = {
	  passwordString(SignPasswordComponents("sign", salt, Crypto.sign(salt + password)))
  }
  
  /**
   * Given a password encoded into a string, return the matching SignPasswordComponents.
   * Return null if the string is not in a recognized format.
   */
  def passwordComponents(passwordHash: String) : SignPasswordComponents = {
    if (passwordHash.length() <= 15) { return null }

    var algo_split_index = passwordHash.indexOf('$')
    if (algo_split_index == -1) { return null }
    
    var algorithm = passwordHash.substring(0,algo_split_index)
    if (algorithm != "sign") { return null }
    
    var salt_start = algo_split_index + 1
    var salt_end = salt_start + 10
    var salt = passwordHash.substring(salt_start, salt_end)

    var hash_start = salt_end
    var hash_end = passwordHash.length()
    var hash = passwordHash.substring(hash_start, hash_end)
    
    SignPasswordComponents("sign", salt, hash)
  }
  
  /**
   * Serialize the specified PasswordComponents to a string,
   * in a manner understood by passwordComponents()
   */
  def passwordString(password: PasswordComponents) : String = {
    password match {
      case SignPasswordComponents(algorithm, salt, hash) =>
        algorithm + "$" + salt + hash
    }
  }
}