package controllers;

import java.util.Date;

import play.i18n.Messages;

import controllers.Authz;
import models.Wiki;
import models.WikiPage;
import scala.Option;
import template.html.*;

public class InlineWiki {
	public static String inlineWiki(String uri, String defaultText) {
		boolean isSuperuser = Authz.isSuperuser();
		
		Option<WikiPage> pageMaybe = Wiki.getPage(uri);
		if (!pageMaybe.isDefined()) {
			// No such page exists
			// Render a generic blank page
			// Superusers should be able to create the page with an editor
			return inline_wiki.render(uri, Messages.get("wiki.empty_page"),
					Option.<Date>empty(), isSuperuser).body();
		} else {
			// The page exists.  Render it!
			WikiPage page = pageMaybe.get();

			return inline_wiki.render(page.uri(), page.content(),
					page.modifiedDate(), isSuperuser).body();
		}
	}
}