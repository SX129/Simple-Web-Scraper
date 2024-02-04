package org.example;
import org.example.data.Product;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void scrapeProductPage(List<Product> productList, Set<String> pagesDiscovered, List<String> pagesToScrape){

        if(!pagesToScrape.isEmpty()) {
            // Remove current web page from scraping queue
            String url = pagesToScrape.remove(0);
            pagesDiscovered.add(url);

            Document doc;
            try {
                // Connects to URL and gets a Jsoup HTML document object
                // Bypass website blockers with userAgent and header
                doc = Jsoup
                        .connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Retrieve list of product HTML elements on current page
            Elements products = doc.select("li.product");

            // Iterate over list of HTML products
            for (Element product : products) {
                Product productObject = new Product();

                // Extract data of interest from HTMl element and store it in new object
                productObject.setUrl(product.selectFirst("a").attr("href"));
                productObject.setImage(product.selectFirst("img").attr("src"));
                productObject.setName(product.selectFirst("h2").text());
                productObject.setPrice(product.selectFirst("span").text());

                // Add new object to list of scraped products
                productList.add(productObject);
            }

            // Retrieve pagination number HTML elements
            Elements paginationElements = doc.select("a.page-numbers");

            // Iterate over the pagination HTML elements
            for (Element pageElement : paginationElements) {
                // New link found
                String pageUrl = pageElement.attr("href");

                // Check if web page is unique and should be scraped
                if (!pagesDiscovered.contains(pageUrl) && !pagesToScrape.contains(pageUrl)) {
                    pagesToScrape.add(pageUrl);
                }

                // Add discovered link into set
                pagesDiscovered.add(pageUrl);
            }

            System.out.println(url + " -> page scraped");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // Initialize list to store Java object of scraped data
        List<Product> productList = Collections.synchronizedList(new ArrayList<>());

        // Initialize set to store unique pages found
        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());

        // Initialize queue of urls to scrape
        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        // Add first page to queue
        pagesToScrape.add("https://scrapeme.live/shop/page/1/");

        // Initialize service to run scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // Launch process to discover some urls and use parallel service
        scrapeProductPage(productList, pagesDiscovered, pagesToScrape);

        // # of iterations executed
        int i = 0;

        // Limit number of pages to scrape
        int limit = 48;

        while(!pagesToScrape.isEmpty() && i < limit){

            // Register the web scraping task
            executorService.execute(() -> scrapeProductPage(productList, pagesDiscovered, pagesToScrape));

            // Delay to avoid overloading server
            TimeUnit.MILLISECONDS.sleep(200);
            i++;
        }

        // Waiting for pending tasks to end
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);

        System.out.println(productList.size());
    }
}