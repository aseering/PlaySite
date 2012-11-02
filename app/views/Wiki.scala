package views

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import controllers.Authz

object Wiki extends Controller {

	def wiki(uri: String) = Action { implicit request =>
		Ok(template.html.wiki("wiki:" + uri));
	}
	
	def postUpdate() = Action { implicit request =>
	    if (!Authz.isSuperuser()) {
	    	BadRequest(template.html.simple_message("Unauthorized", "You are not authorized to modify this page.", views.routes.Application.index.url, "the homepage"))
	    } else {
	        val user = Authz.getUsername().get
	    	Form(tuple(
	    			"wikitext" -> text,
	    			"wikiuri" -> nonEmptyText,
	    			"origpage" -> nonEmptyText
    			)).bindFromRequest.fold(
					errors => BadRequest(template.html.simple_message("Invalid Wiki POST", "We've received an invalid Wiki submission.  This is either due to a website bug or to an attempt to submit changes via an external script.", views.routes.Application.index.url, "the homepage")),
					wikipage => {
						val wikitext = wikipage._1;
						val wikiuri = wikipage._2;
						val orig_url = wikipage._3;
		            
						models.Wiki.addPage(wikiuri, wikitext, user)
						
						Redirect(orig_url)
		        })
	    }
	}
}