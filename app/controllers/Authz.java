package controllers;

import play.mvc.Http.Context;
import play.mvc.Http.Session;

/**
 * Authorization ("Authz") helper methods
 * 
 * Authentication is proving who you are.
 * Authorization is, given who you are, proving that you're allowed access.
 * 
 * @author Adam Seering
 */
class Authz {
	/**
	 * @return true iff the currently-logged-in user is a superuser.
	 */
	public static boolean isSuperuser() {
		Session session = Context.current().session();
		// Careful -- session.get() can return null;
		// null.equals(foo) is not defined, but foo.equals(null) is.
		return "true".equals(session.get("is_superuser"));
	}
	
	/**
	 * @return true iff there is currently a user logged in
	 */
	public static boolean isLoggedIn() {
		Session session = Context.current().session();
		return session.get("email") != null;
	}
}