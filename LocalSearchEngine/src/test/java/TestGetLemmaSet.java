import main.Lemmatisator.Lemmatisator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;


public class TestGetLemmaSet {

    @Test
    public void testGetLemmaSet() throws IOException {
        Lemmatisator lemmatisator = new Lemmatisator();
        String url = "https://lenta.ru";
        Connection connection = Jsoup.connect(url).ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");
        Document document = connection.get();
        String bodyText = document.body().text();
        String titleText =  document.title();
        System.out.println(lemmatisator.getLemmaSet(bodyText + " " + titleText));
    }

    @Test
    public void testCreateIndex(){


    }
}
