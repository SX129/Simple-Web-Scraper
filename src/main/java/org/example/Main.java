package org.example;
import org.example.data.Product;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Document doc;

        try{
            // Connects to URL and gets a Jsoup HTML document object
            // Bypass website blockers with userAgent and header
            doc = Jsoup
                    .connect("https://scrapeme.live/shop")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        // Retrieve list of product HTML elements
        Elements products = doc.select("li.product");

        // Initialize list to store Java object of scraped data
        List<Product> productList = new ArrayList<>();

        // Iterate over list of HTML products
        for(Element product: products){
            Product productObject = new Product();

            // Extract data of interest from HTMl element and store it in new object
            productObject.setUrl(product.selectFirst("a").attr("href"));
            productObject.setImage(product.selectFirst("img").attr("src"));
            productObject.setName(product.selectFirst("h2").text());
            productObject.setPrice(product.selectFirst("span").text());

            // Add new object to list of scraped products
            productList.add(productObject);
        }

        productList.toString();
        System.out.println(productList);
    }
}