import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

import controllers.SendMail

import javax.mail._
import javax.mail.internet._

class SendMailSpec extends Specification {
	"SendMail" should {
		"emit the appropriate MIME object to the Mail framework when called" in {
			SendMail.sendMail("recipient@example.com", "sender@example.com", 
					"Sample e-mail", "This is a sample e-mail", false,
					(m : Message) => {
						m.getFrom().length must equalTo(1)
						m.getFrom()(0).asInstanceOf[InternetAddress].getAddress() must equalTo("sender@example.com")
						m.getAllRecipients().length must equalTo(1)
						m.getAllRecipients()(0).asInstanceOf[InternetAddress].getAddress() must equalTo("recipient@example.com")
						m.getSubject() must equalTo("Sample e-mail")
						m.getContent().toString() must equalTo("This is a sample e-mail")
						m.getContentType() must equalTo("text/plain")
		     }) must not be equalTo("Done!")  // HACK to make the type system happy
	 	}
		
		"handle HTML e-mail" in {
			SendMail.sendMail("recipient@example.com", "sender@example.com", 
					"Sample e-mail", "<html>This is a sample e-mail</html>", true,
					(m : Message) => {
						m.getFrom().length must equalTo(1)
						m.getFrom()(0).asInstanceOf[InternetAddress].getAddress() must equalTo("sender@example.com")
						m.getAllRecipients().length must equalTo(1)
						m.getAllRecipients()(0).asInstanceOf[InternetAddress].getAddress() must equalTo("recipient@example.com")
						m.getSubject() must equalTo("Sample e-mail")
						m.getContent().toString() must equalTo("<html>This is a sample e-mail</html>")
						m.getContentType() must startWith("text/html")  // Java may generate a longer content-type string for us to describe the character encoding.  Don't worry about it for now.
		     }) must not be equalTo("Done!")  // HACK to make the type system happy
	 	}
	}		
}