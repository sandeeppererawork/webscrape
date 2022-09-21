package com.speer.qe.function;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Scraper {

    /** integer n is a random number between 0-20*/
    static int n = new Random().nextInt(20-1) + 1;
    static int i = 0;
    static LinkedList wikiLinks = new LinkedList();
    static List uniqueWikiLinks = new LinkedList();
    static String protocol = "";
    static String host = "";
    static HtmlPage page;

    /** This method accepts a parameter link and scrape it
     * @param url
     * */
    public static void scrape(String url) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        try {
            page = webClient.getPage(url);
            protocol = page.getUrl().getProtocol();
            host = page.getUrl().getHost();
            webClient.close();
            /** scraping the parameter link and retrieving all the anchot tags in page */
            List<HtmlAnchor> links = page.getAnchors();
            for (HtmlAnchor link : links) {
                /** storing only the Wiki links in a List*/
                if (link.getHrefAttribute().contains(protocol+"://"+host)) {
                    String href = link.getHrefAttribute();
                    /** adding links to list*/
                    wikiLinks.add(href);
                }
            }
            /** making the links list unique therefore code won't go through the same link twice */
            uniqueWikiLinks = (List) wikiLinks.stream().distinct().collect(Collectors.toList());

            /** traversing through the links in parameterized link's page n times t*/
            while (i<n){
                page = webClient.getPage(uniqueWikiLinks.get(i).toString());
                webClient.close();
                List<HtmlAnchor> linksNested = page.getAnchors();
                for (HtmlAnchor link : linksNested) {
                    if (link.getHrefAttribute().contains(protocol+"://"+host)) {
                        String href = link.getHrefAttribute();
                        /** adding the links to the same list */
                        wikiLinks.add(href);
                    }
                }
                i++;
            }
            /** storing the links, links count and unique links count in CSV file */
            FileWriter wikiLinksCSV = new FileWriter("WikiLinks.csv", false);
            wikiLinksCSV.write(wikiLinks + "," + wikiLinks.size() +","+ uniqueWikiLinks.size());
            wikiLinksCSV.close();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    public static void main(String args[]){
        scrape("https://en.wikipedia.org/wiki/Liz_Truss");
    }
}
