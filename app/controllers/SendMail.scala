package controllers

import java.util._
import javax.mail._
import javax.mail.internet._
import javax.activation._

object SendMail {
  
	var sender = { msg : Message => Transport.send(msg) }
  
	/**
	 * Send an e-mail, with the specified headers
	 */
	def sendMail(to : String, from : String, subject : String, body : String, isHTML : Boolean = false, sender : ((Message) => Any) = SendMail.sender /* FOR TESTING ONLY */)
	{
		// Get system properties
		val properties = System.getProperties()

		// Get the default Session object.
		val session = Session.getDefaultInstance(properties)
	
		// Create a default MimeMessage object.
		val message = new MimeMessage(session)
	
		// Set From: header field of the header.
		message.setFrom(new InternetAddress(from))
	
		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO,
				new InternetAddress(to))
	
		// Set Subject: header field
		message.setSubject(subject)
	
		if (isHTML) {
			// Send the complete message parts
			message.setContent(body, "text/html")
			message.saveChanges()
		} else {
			message.setText(body)
		}
	
		// Send message
		sender(message)
	}
}