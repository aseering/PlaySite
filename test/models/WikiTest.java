package models;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import models.Wiki;
import models.WikiPage;
import scala.Option;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

// TODO:  Write tests in Scala
// Should do this once the ScalaTest framework/IDE
// is ported to this version of Eclipse (probably soon?)
public class WikiTest {

	/**
	 * Assert that d1 and d2 are "near" each other.
	 * This is intended to verify that Wiki pages correctly indicate that
	 * they were created "now", for some reasonable definition of "now"
	 * given that we check the system time independently of PostgreSQL.
	 * 
	 * @throws assertion if d1 and d2 are more than 10 seconds apart
	 */
	void assertNear(Date d1, Date d2) {
		// 
		assertTrue(Math.abs(d1.getTime() - d2.getTime()) < 10000);
	}
	
	@Test
	public void testAddDropWikiPage() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	        public void run() {
				// Very simple test; make sure we don't assert.
				// We can't really verify without testing our getters,
				// which is up next.
				Wiki.createPage("index", "This is a generic welcome page", "testUser@example.com");
				Wiki.deletePage("index");
	        }});
	}

	@Test
	public void testGetUpdateWikiPage() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	        public void run() {
	        	// Should not be able to get a Wiki page that doesn't exist yet
	        	assertFalse(Wiki.getPage("index").isDefined());

	        	Option<WikiPage> pageMaybe;
	        	WikiPage page;
	        	
	        	// Create a page; make sure we can fetch it and that all values are correct (or at least defined...)
	        	Wiki.createPage("index", "This is a generic welcome page", "testUser@example.com");
	        	pageMaybe = Wiki.getPage("index");
	        	assertTrue(pageMaybe.isDefined());
	        	page = pageMaybe.get();
	        	assertEquals(page.uri(), "index");
	        	assertEquals(page.content(), "This is a generic welcome page");
	        	assertEquals(page.author(), "testUser@example.com");
	        	assertNear(page.createDate(), new Date());  // 
	        	assertFalse(page.updater().isDefined());
	        	assertTrue(page.modifiedDate().isDefined());
	        	assertNear(page.modifiedDate().get(), new Date());  // 

	        	// Update the page; make sure the updates stick
	        	Wiki.updatePage("index", "This is another generic welcome page", "testUser2@example.com");
	        	pageMaybe = Wiki.getPage("index");
	        	assertTrue(pageMaybe.isDefined());
	        	page = pageMaybe.get();
	        	assertEquals(page.uri(), "index");
	        	assertEquals(page.content(), "This is another generic welcome page");
	        	assertEquals(page.author(), "testUser@example.com");
	        	assertNear(page.createDate(), new Date());  // 
	        	assertTrue(page.updater().isDefined());
	        	assertEquals(page.updater().get(), "testUser2@example.com");
	        	assertTrue(page.modifiedDate().isDefined());
	        	assertNear(page.modifiedDate().get(), new Date());
	        	
	        	// Delete the page; make sure it goes away
	        	Wiki.deletePage("index");
	        	assertFalse(Wiki.getPage("index").isDefined());
	        }});
	}
	
}
