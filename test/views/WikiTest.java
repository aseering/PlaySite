package views;

import org.junit.Test;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.FIREFOX;
import static play.libs.F.Callback;
import static org.fest.assertions.Assertions.assertThat;
import play.test.TestBrowser;
import static utils.Constants.TEST_URL;
import static utils.Helpers.loginAsSuperuser;
import static utils.Helpers.logout;
import play.i18n.Messages;
import play.i18n.Lang;

// TODO:  Write tests in Scala
// Should do this once the ScalaTest framework/IDE
// is ported to this version of Eclipse (probably soon?)
public class WikiTest {

	@Test
	public void testViewMainWikiPage() {
	    running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	        public void invoke(TestBrowser browser) {
	        	browser.goTo(TEST_URL + "/wiki"); 
	        	assertThat(browser.$("[id=\"id_wiki:index.html\"] div.inline_wiki_inner").getTexts().get(0)).isEqualTo(Messages.get(Lang.forCode("en"), "wiki.empty_page"));
	        }
	    });
	}
	
	@Test
	public void testViewWikiPage() {
	    running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
	        public void invoke(TestBrowser browser) {
	           browser.goTo(TEST_URL + "/wiki/test"); 
	           assertThat(browser.$("[id=\"id_wiki:test\"] div.inline_wiki_inner").getTexts().get(0)).isEqualTo(Messages.get(Lang.forCode("en"), "wiki.empty_page"));
	        }
	    });
	}
	
	@Test
	public void testAddModifyWikiPage() {
		// Run JS tests in a real browser
		// TODO:  Automate running in various browsers if/as available
		// (Someone probably has a plugin for this?  If not, probably not hard to write;
		// there are a bunch of stock drivers, just make a 'for' loop over them?)
	    running(testServer(3333, fakeApplication(inMemoryDatabase())), FIREFOX, new Callback<TestBrowser>() {
	        public void invoke(TestBrowser browser) {
	           browser.goTo(TEST_URL + "/wiki/test"); 
	           assertThat(browser.$("[id=\"id_wiki:test\"] div.inline_wiki_inner").getTexts().get(0)).isEqualTo(Messages.get(Lang.forCode("en"), "wiki.empty_page"));

	           //browser.goTo(TEST_URL + "/wiki/test");

	           loginAsSuperuser(browser);

	           browser.goTo(TEST_URL + "/wiki/test");
	           browser.executeScript("$('[id=\"id_wikitext_wiki:test_ifr\"]').contents()[0].getElementById('tinymce').innerHTML='Sample Text';");
	           //browser.$("[id=\"id_wikitext_wiki:test\"]").text("Sample Text");
	           browser.$("[id=\"id_wiki_submit_wiki:test\"]").click();
	           
	           logout(browser);

	           browser.goTo(TEST_URL + "/wiki/test");
	           assertThat(browser.$("[id=\"id_wiki:test\"] div.inline_wiki_inner p").getTexts().get(0)).isEqualTo("Sample Text");
	           
	           // Cleanup
	           models.Wiki.deletePage("wiki:test");
	        }
	    });
	}
	
}
