package controllers

import play.api.mvc.RequestHeader

/**
 * Authorization ("Authz") helper methods
 * 
 * Authentication is proving who you are.
 * Authorization is, given who you are, proving that you're allowed access.
 * 
 * @author Adam Seering
 */
object Authz {

    /**
     * Return the currently-logged-in user's username,
     * if anyone is logged in
     */
    def getUsername()(implicit request: RequestHeader): Option[String] = {
        request.session.get("email")
    }
    
	/**
	 * @return true iff the currently-logged-in user is a superuser.
	 */
	def isSuperuser()(implicit request: RequestHeader): Boolean = {
		// Careful -- session.get() can return null;
		// null.equals(foo) is not defined, but foo.equals(null) is.
		val is_superuser = request.session.get("is_superuser")
	    return is_superuser.isDefined && is_superuser.get == "true"
	}
	
	/**
	 * @return true iff there is currently a user logged in
	 */
	def isLoggedIn()(implicit request: RequestHeader): Boolean = {
		return request.session.get("email") != null
	}
}