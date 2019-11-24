package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiMediator;
import fastily.jwiki.core.Wiki;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Tests {

	@Test
	public void zeitgeistTest(){
		List<String> results = new ArrayList<>();
		Wiki wiki = new Wiki("en.wikipedia.org");
		WikiMediator myMediator = new WikiMediator();

		myMediator.simpleSearch("US", 5);
		myMediator.simpleSearch("Canada", 5);
		myMediator.simpleSearch("Canada", 5);
		myMediator.simpleSearch("Canada", 5);
		results.add("Canada");
		results.add("US");

		System.out.println(myMediator.zeitgeist(5));
		assertEquals(results, myMediator.zeitgeist(5));
	}

}
