package models

import play.api.Play.current
import play.api.db._

import anorm._
import anorm.SqlParser._

import java.util.Date

case class WikiPage(id: Pk[Long],
        uri: String, content: String,
        author: String, createDate: Date,
        updater: Option[String], modifiedDate: Option[Date])

object Wiki {
    val simple = {
        get[Pk[Long]]("wikipages.id") ~
        get[String]("wikipages.uri") ~
        get[String]("wikipages.content") ~
        get[String]("wikipages.author") ~
        get[Date]("wikipages.create_date") ~
        get[Option[String]]("wikipages.updater") ~
        get[Option[Date]]("wikipages.modified_date") map {
            case id~uri~content~author~createDate~updater~modifiedDate =>
                WikiPage(id, uri, content, author, createDate, updater, modifiedDate)
        }
    }
    
    /**
     * Fetch the specified page by its URI.
     * 
     * Note that a URI is '''not''' a URL!
     * A URI is simply a unique string that identifies a page.
     *
     * URIs are typically embedded in the code that renders the page.
     * By convention, they should be in something resembling Java or Python
     * dot-notation, ie., "program.index" might be the global Program homepage.
     * 
     * Because they really can be arbitrary strings, the Wiki section gets a different schema:
     * "wiki:<page URL>" will map to something like @{views.routes.Wiki.wiki + "/" + <page URL>}
     */
	def getPage(uri: String) : Option[WikiPage] = {
	    DB.withConnection { implicit connection => 
	    	SQL("select * from wikipages where uri = {uri};").on('uri -> uri).as(Wiki.simple.singleOpt)
	    }
	}
  
	/**
	 * Create the specified page.
	 * 
	 * 'author' is the email of the User who created the page,
	 * or a well-defined string (preferably listed below) if updated by an automated process.
	 * 
	 * 'createDate' is automatically set to the current date/time.
	 * 
	 * 'updater' and 'modified_date' are left undefined.  They can only be set by updatePage().
	 */
	def createPage(uri: String, content: String, author: String) {
	    DB.withConnection { implicit connection => 
	    	SQL("insert into wikipages (uri, content, author) values ({uri}, {content}, {author});").on(
	    	        'uri -> uri,
	    	        'content -> content,
	    	        'author -> author
	    			).executeInsert()
	    }
	}
	
	/**
	 * Update the page at the specified URI.  Set the specified title, content, and updater.
	 * 
 	 * 'updater' is the email of the User who edited the page,
	 * or a well-defined string (preferably listed below) if updated by an automated process.
	 * 'modified_date' is set to the current date/time.
	 * 
	 * It is not possible to update the author or createDate of a page.
	 * These always reflect the initial creation.
	 * To update them, delete and re-create the page.
	 */
	def updatePage(uri: String, newContent: String, updater: String) {
	    DB.withConnection { implicit connection => 
	    	SQL("update wikipages set content = {content}, updater = {updater}, modified_date = {modified_date} where uri = {uri};").on(
	    	        'uri -> uri,
	    	        'content -> newContent,
	    	        'updater -> updater,
	    	        'modified_date -> new Date()
	    			).executeUpdate()
	    }
	}
	
	/**
	 * Delete the specified page.
	 */
	def deletePage(uri: String) {
	    DB.withConnection { implicit connection => 
	    	SQL("delete from wikipages where uri = {uri};").on('uri -> uri).execute()
	    }
	}
}
