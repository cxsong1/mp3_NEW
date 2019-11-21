package cpen221.mp3.wikimediator;
import fastily.jwiki.core.*;

public class jwikiEX {
        public static void main(String[] args){
            Wiki wiki = new Wiki("en.wikipedia.org");
            System.out.println(wiki.getRandomPages(5, NS.MAIN));
        }
    }
