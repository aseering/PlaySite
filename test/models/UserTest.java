package models;

import static org.junit.Assert.*;
import org.junit.Test;
import models.User;
import scala.Option;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

// TODO:  Write tests in Scala
// Should do this once the ScalaTest framework/IDE
// is ported to this version of Eclipse (probably soon?)
public class UserTest {

	@Test
	public void testAddDropUser() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	        public void run() {
				// Very simple test; make sure we don't assert.
				// We can't really verify without testing our getters,
				// which is up next.
				User.addUser("testADU@example.com", "password", false);
				User.dropUser("testADU@example.com", Option.apply("password"));
	        }});
	}
	
	@Test
	public void testAddDropFindByEmail() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	        public void run() {
	        	Option<User> u;
				
				// Make a user; make sure we can find it
				User.addUser("testFBE1@example.com", "password", false);
				u = User.findByEmail("testFBE1@example.com");
				assertTrue(u.isDefined());
				assertEquals(u.get().email(), "testFBE1@example.com");
				
				// Make another user; make sure we get the right one back
				User.addUser("testFBE2@example.com", "password2", false);
				u = User.findByEmail("testFBE2@example.com");
				assertTrue(u.isDefined());
				assertEquals(u.get().email(), "testFBE2@example.com");
				
				// Make sure we can't find a user that doesn't exist
				u = User.findByEmail("testFBE3@example.com");
				assertFalse(u.isDefined());
				
				// Drop a user; make sure only that user goes away
				User.dropUser("testFBE2@example.com", Option.apply("password2"));
				u = User.findByEmail("testFBE2@example.com");
				assertFalse(u.isDefined());
				u = User.findByEmail("testFBE1@example.com");
				assertTrue(u.isDefined());
				assertEquals(u.get().email(), "testFBE1@example.com");
				
				// Finish cleaning up; make sure no one's home
				User.dropUser("testFBE1@example.com", Option.apply("password"));
				u = User.findByEmail("testFBE1@example.com");
				assertFalse(u.isDefined());
				u = User.findByEmail("testFBE2@example.com");
				assertFalse(u.isDefined());
				u = User.findByEmail("testFBE3@example.com");
				assertFalse(u.isDefined());
	        }});
	}
	
	@Test
	public void testCheckPassword() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	        public void run() {
				// Simple positive and negative tests
				User.addUser("testLogin@example.com", "pass1234", false);
				assertTrue(User.login("testLogin@example.com", "pass1234").isDefined());
				assertFalse(User.login("testLogin@example.com", "This is the wrong password").isDefined());
				
				// Make sure we don't match some other user
				User.addUser("testLogin2@example.com", "pass123456", false);
				assertTrue(User.login("testLogin2@example.com", "pass123456").isDefined());
				assertFalse(User.login("testLogin2@example.com", "pass1234").isDefined());
				
				// Make sure we're not storing any passwords in plain text
				Option<User> ou = User.findByEmail("testLogin2@example.com");
				assertTrue(ou.isDefined());
				User u = ou.get();
				assertEquals(u.password().indexOf("pass1234"), -1);
				
				// Clean up
				User.dropUser("testLogin@example.com", Option.apply("pass1234"));
				User.dropUser("testLogin2@example.com", Option.apply("pass123456"));
	        }});
	}
	
	@Test
	public void testMakeSuperuser() {
	    running(fakeApplication(inMemoryDatabase()), new Runnable() {
	    	public void run() {
	    		Option<User> ou;
	    		
	    		// Create an ordinary user
				User.addUser("testLogin@example.com", "pass1234", false);
				
				// Make sure the user is not a superuser
				ou = User.findByEmail("testLogin@example.com");
				assertFalse(ou.get().is_superuser());
				
				// Create a superuser
				User.addUser("testLogin2@example.com", "pass1234", true);

				// Make sure the user is still not a superuser
				ou = User.findByEmail("testLogin@example.com");
				assertFalse(ou.get().is_superuser());
				
				// Make sure the new user is a superuser
				ou = User.findByEmail("testLogin2@example.com");
				assertTrue(ou.get().is_superuser());
				
				User.dropUser("testLogin@example.com", Option.apply("pass1234"));
				User.dropUser("testLogin2@example.com", Option.apply("pass1234"));				
	    	}});
	}

}
