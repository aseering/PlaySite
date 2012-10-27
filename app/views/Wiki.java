package views;

import play.mvc.*;

public class Wiki extends Controller {
	public static Result wiki(String uri) {
		return ok(template.html.wiki.render("wiki:" + uri));
	}
	
}