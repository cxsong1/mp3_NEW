package cpen221.mp3;

import cpen221.mp3.cache.Cache;
import cpen221.mp3.wikimediator.WikiMediator;
import fastily.jwiki.core.Wiki;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tests {

	@Test
	public void zeitgeistTest(){
		List<String> results;
		WikiMediator myMediator = new WikiMediator();

		myMediator.simpleSearch("Canada", 5);
		myMediator.simpleSearch("Canada", 5);
		myMediator.simpleSearch("Canada", 5);
		myMediator.simpleSearch("US", 5);

		results = new ArrayList<>();
		results.add("Canada");
		results.add("US");

		List<String> zeit = myMediator.zeitgeist(5);

		for (String s: zeit){
			System.out.println(s);
		}

		Assert.assertEquals(results, myMediator.zeitgeist(5));
	}

	@Test
	public void testGetConnectedPages1() {
		WikiMediator wikiMediator = new WikiMediator();
		List<String> connectedPageTitles = wikiMediator.getConnectedPages("Joker", 2);
		/*
		for (String s: connectedPageTitles){
			System.out.println(s);
		}
		 */
		System.out.println(connectedPageTitles.size());
		assertTrue(connectedPageTitles.size() > 0);
	}

	@Test
	public void testGetConnectedPages2() {
		WikiMediator wikiMediator = new WikiMediator();
		List<String> connectedPageTitles = wikiMediator.getConnectedPages("Lunari", 1);

		for (String s: connectedPageTitles){
			System.out.println(s);
		}

		System.out.println(connectedPageTitles.size());

		assertTrue(connectedPageTitles.size() > 0);
	}

	@Test
	public void testGetConnectedPages3() {
		WikiMediator wikiMediator = new WikiMediator();
		List<String> connectedPageTitles = wikiMediator.getConnectedPages("Joker", 1);
		/*
		for (String s: connectedPageTitles){
			System.out.println(s);
		}
		 */
		System.out.println(connectedPageTitles.size());
		System.out.println(connectedPageTitles);
		assertTrue(connectedPageTitles.size() > 0);
	}

	@Test
	public void testGetConnectedPages4() {
		WikiMediator wikiMediator = new WikiMediator();
		List<String> connectedPageTitles = wikiMediator.getConnectedPages("Joker", 1);
		ArrayList<String> summation = new ArrayList<>(connectedPageTitles);

		for (String s: connectedPageTitles){
			List<String> each = wikiMediator.getConnectedPages(s, 1);
			for (String notDuplicate: each){
				if (!summation.contains(notDuplicate)){
					summation.add(notDuplicate);
					System.out.println(notDuplicate + ", " + each.size());
				}
			}
		}
		// why doesn't stack and queue return the same size?
		System.out.println(summation.size());
	}

	@Test
	public void testTrending() throws InterruptedException {
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		wikiMediator.simpleSearch("Canada", 5);
		wikiMediator.simpleSearch("Canada", 5);

		Thread.sleep(30001);

		wikiMediator.simpleSearch("US", 1);
		wikiMediator.simpleSearch("US", 1);
		wikiMediator.simpleSearch("Trophy", 1);

		List<String> expected = new ArrayList<>();
		expected.add("US");
		expected.add("Trophy");

		assertEquals(expected, wikiMediator.trending(5));
	}

	@Test
	public void testTrending2(){
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		wikiMediator.simpleSearch("Rabbit", 5);
		wikiMediator.simpleSearch("Canoe", 5);

		wikiMediator.simpleSearch("US", 1);
		wikiMediator.simpleSearch("Delta", 1);
		wikiMediator.simpleSearch("Trophy", 1);

		List<String> expected = new ArrayList<>();
		expected.add("Canada");
		expected.add("Trophy");
		expected.add("Delta");
		expected.add("Rabbit");

		assertEquals(expected, wikiMediator.trending(4));
	}

	@Test
	public void testPeakLoad30s1(){
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);

		int count = wikiMediator.peakLoad30s();

		assertEquals(1, count);
	}

	@Test
	public void testPeakLoad30s2(){
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		wikiMediator.getPage("Joker");

		int count = wikiMediator.peakLoad30s();

		assertEquals(2, count);
	}

	@Test
	public void testPeakLoad30s3() throws InterruptedException {
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		// sleep 30s
		Thread.sleep(30001);
		wikiMediator.getPage("Joker");

		int count = wikiMediator.peakLoad30s();

		assertEquals(1, count);
	}

	@Test
	public void testPeakLoad30s4() throws InterruptedException {
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		// sleep 10s
		Thread.sleep(10000);
		wikiMediator.getPage("Joker");
		wikiMediator.zeitgeist(5);

		int count = wikiMediator.peakLoad30s();

		assertEquals(3, count);
	}

	@Test
	public void testPeakLoad30s5() throws InterruptedException {
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.simpleSearch("Canada", 5);
		// sleep 30s
		Thread.sleep(30000);
		wikiMediator.getPage("Joker");
		wikiMediator.zeitgeist(5);

		int count = wikiMediator.peakLoad30s();

		assertEquals(2, count);
	}

	@Test
	public void testCache(){
		WikiMediator wikiMediator = new WikiMediator();

		wikiMediator.getPage("Canada");
		wikiMediator.getPage("Barack Obama");
		wikiMediator.getPage("Canada");
	}

}
