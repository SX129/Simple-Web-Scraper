package org.example;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Document doc;

        try{
            // Connects to URL and gets a Jsoup HTML document object.
            // Bypass website blockers with userAgent and header.
            doc = Jsoup
                    .connect("https://scrapeme.live/shop")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}