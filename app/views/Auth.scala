package views

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.openid.OpenID
import play.api.libs.concurrent.Redeemed
import play.api.libs.concurrent.Thrown
import scala.util.Random
import utils.RandomHelpers
import models.User
import controllers.SendMail
import play.api.i18n.Messages

object Auth extends Controller {
  
	/** Returns a pseudorandomly generated String drawing upon
	 *  only ASCII characters between 33 and 126.
	 */
	def nextASCIIString(length: Int) = {
		val (min, max) = (33, 126)
		def nextDigit = Random.nextInt(max - min) + min
			
		new String(Array.fill(length)(nextDigit.toByte), "ASCII")
	}
	
	def logout = Action { implicit request => 
	  	var loginMaybe = request.session.get("email")
		if (loginMaybe.isDefined) {
			Ok(template.html.logout(loginMaybe.get))
		} else {
			BadRequest(template.html.logout())
		}
	}
	
	def logoutDo = Action { implicit request =>
		Ok(template.html.simple_message(
				Messages("logout.title"),
				Messages("logout.message"),
				views.routes.Application.index.toString(),
				Messages("logout.goto_text"))).withSession(session - "email")
	}
	  
	def login = Action { implicit request =>
		var loginMaybe = request.session.get("email")
		if (loginMaybe.isDefined) {
			// Already logged in; why are you here?
  			Ok(template.html.simple_message(
  					Messages("login.already_logged_in.title"),
  					Messages("login.already_logged_in.message", loginMaybe.get),
  					views.routes.Application.index.toString(),
  					Messages("login.already_logged_in.goto_text")))
		} else {
			Ok(template.html.login(Messages("login.greeting")))
		}
	}
	
	def loginUsername = Action { implicit request =>
		Form("email" -> nonEmptyText).bindFromRequest.fold(
		    errors => BadRequest(template.html.login(Messages("login.greeting"), Messages("login.bad_email"))),
		    email => {
		    	if (User.findByEmail(email).isDefined) {
		    		Ok(template.html.login_pw_prompt(Messages("login.pw_prompt"), email))
		    	} else {
					// We're making a new account!
					// Start by creating a new password
					var new_password = RandomHelpers.nextASCIIString(10)

					// Now create a User record with that password
					User.addUser(email, new_password)

					// Now, send the password to the user
					// Get system properties used to customize the e-mail
					var from_addr = Play.current.configuration.getString("mail.default_from_address").get
					var site_name = Messages("site.name")
					SendMail.sendMail(from_addr, email, Messages("login.email_subject", site_name), template.html.welcome_email(site_name, new_password).body)
		    	  
		    		Ok(template.html.login_pw_prompt(Messages("login.sent_pw_mail"), email))
		    	}
		    }
		    )
	}
	
	def loginSubmit = Action { implicit request =>
	  	Form(tuple(
	  			"email" -> nonEmptyText,
	  			"password" -> nonEmptyText
	  	) verifying("Invalid password", result => result match {
	  	  	case (e, p) => (User.checkPassword(e,p))
	  	})).bindFromRequest.fold(
		    errors => BadRequest(template.html.login(Messages("login.greeting"), Messages("login.bad_password"))),
  			user => {
				if (User.checkPassword(user._1, user._2)) {
					// Now send them to the waiting page to log in when they're ready
					Redirect(routes.Application.index).withSession("email" -> user._1)
				} else {
					// Error out if the user already exists
					// We shouldn't get here unless something is wrong...
					BadRequest(template.html.login(Messages("login.greeting"), Messages("login.bad_password")))	
  				}
  			}
		)
	}
}