import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebScraper {
    public static void main(String[] args) {
        String url = "https://www.bbc.com";
        List<NewsArticle> articles = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            Elements newsItems = doc.select("a[href*='/news/']");

            for (Element item : newsItems) {
                String headline = item.text();
                String link = item.absUrl("href");

                try {
                    Document articlePage = Jsoup.connect(link).get();
                    String pubDate = articlePage.selectFirst("time") != null
                            ? articlePage.selectFirst("time").text()
                            : "N/A";

                    String author = articlePage.selectFirst("[rel=author], .ssrcss-1rv5c1k-Contributor") != null
                            ? articlePage.selectFirst("[rel=author], .ssrcss-1rv5c1k-Contributor").text()
                            : "N/A";

                    articles.add(new NewsArticle(headline, pubDate, author));

                } catch (IOException e) {
                    System.out.println("Couldn't fetch article: " + link);
                }
            }

        } catch (IOException e) {
            System.out.println("Error connecting to BBC: " + e.getMessage());
        }

        for (NewsArticle article : articles) {
            System.out.println(article);
        }
    }
}