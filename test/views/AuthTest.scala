import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

import controllers.SendMail
import models.User

import javax.mail._
import javax.mail.internet._

class AuthTest extends Specification {
	"Auth" should {
		"create a new user account" in {
		    running(FakeApplication()) {
				val login_result = views.Auth.login()(FakeRequest())
				
				status(login_result) must equalTo(OK)
				contentAsString(login_result) must contain("name=\"email\"")
				
				// Temporarily override temp_email_fn for the duration of the test
				// so that we can capture the e-mail containing our password
				val temp_email_fn = SendMail.sender
				
				SendMail.sender = { m : Message =>				
					val password = m.getContent().toString().split("\n")(5) // HARDCODED; need a better way to pick out the password
					
					val login_submit = views.Auth.loginSubmit()(FakeRequest() withFormUrlEncodedBody(
						"email" -> "testUser@example.com",
					    "password" -> password
					    ))
				    
					session(login_submit).get("email").isDefined must be equalTo(true)
					session(login_submit).get("email").get must be equalTo("testUser@example.com")
				}
	
				val login_username = views.Auth.loginUsername()(FakeRequest() withFormUrlEncodedBody(
						"email" -> "testUser@example.com"
					))
				
				status(login_username) must equalTo(OK)
				contentAsString(login_username) must contain("name=\"password\"")
				
				val userMaybe = User.findByEmail("testUser@example.com")

				userMaybe.isDefined must be equalTo(true)
				userMaybe.get.email must be equalTo("testUser@example.com")
				userMaybe.get.is_superuser must be equalTo(false)
				
				val login_submit_failed = views.Auth.loginSubmit()(FakeRequest() withFormUrlEncodedBody(
					"email" -> "testUser@example.com",
				    "password" -> "fake_password"
				    ))
				    
				session(login_submit_failed).get("email").isDefined must be equalTo(false)
				
				// Drop the user from the table;
				// make sure that we can re-create it
				User.dropUser("testUser@example.com")
				
				SendMail.sender = { m : Message =>				
					val password = m.getContent().toString().split("\n")(5) // HARDCODED; need a better way to pick out the password
					
					password must not be equalTo(null)
					password must not be equalTo("")
					
					val login_submit = views.Auth.loginSubmit()(FakeRequest() withFormUrlEncodedBody(
						"email" -> "testUser@example.com",
					    "password" -> password
					    ))
				    
					session(login_submit).get("email").isDefined must be equalTo(true)
					session(login_submit).get("email").get must be equalTo("testUser@example.com")
				}
	
				val login_username2 = views.Auth.loginUsername()(FakeRequest() withFormUrlEncodedBody(
						"email" -> "testUser@example.com"
					))
					
				SendMail.sender = temp_email_fn

				val userMaybe2 = User.findByEmail("testUser@example.com")
				User.dropUser("testUser@example.com")

				userMaybe2.isDefined must be equalTo(true)
				userMaybe2.get.email must be equalTo("testUser@example.com")
				userMaybe2.get.is_superuser must be equalTo(false)
			}
		}

		"work in a Web browser" in {
			running(TestServer(3333), HTMLUNIT) { browser =>
			  	// Lighter-weight test; not testing all the negative cases here
			  	browser.goTo("http://localhost:3333/login")
			  	browser.pageSource() must contain("name=\"email\"")
			  	
			  	// Temporarily override temp_email_fn for the duration of the test
				// so that we can capture the e-mail containing our password
				val temp_email_fn = SendMail.sender
				
				SendMail.sender = { m : Message =>				
					val password = m.getContent().toString().split("\n")(5) // HARDCODED; need a better way to pick out the password
					
				  	browser.url must equalTo("http://localhost:3333/login_username")

					browser.$("#id_password").text(password)
					browser.$("#id_submit").click()
					
					var seenCookie = false
					
					browser.url must equalTo("http://localhost:3333/login_submit")
					browser.getCookie("email") must equalTo("testBrowserUser@example.com")
					
					browser.goTo("http://localhost:3333/logout")
				  	browser.$("#id_submit").click()

				  	browser.getCookie("email") must equalTo("")
				}

			  	browser.$("#id_email").text("testBrowserUser@example.com")			  	
			  	browser.$("#id_submit").click()
			  	browser.url must equalTo("http://localhost:3333/login_username")
				SendMail.sender = temp_email_fn

				User.dropUser("testBrowserUser@example.com")
			}
		}
	}		
}