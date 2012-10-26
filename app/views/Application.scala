package views

import play.api._
import play.api.mvc._

import templates._

object Application extends Controller {
  
  def index = Action {
    Ok(template.html.index("Your new application is ready."))
  }
 
}