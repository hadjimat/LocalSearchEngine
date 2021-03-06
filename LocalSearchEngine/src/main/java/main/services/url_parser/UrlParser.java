package main.services.url_parser;

import lombok.Setter;
import main.lemmatisator.Lemmatisator;
import main.model.Lemma;
import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
import main.services.IndexService;
import main.services.LemmaService;
import main.services.PageService;
import main.services.SiteService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RecursiveAction;


@Setter
public class UrlParser extends RecursiveAction {
    private final static String urlRegex = "(?i).*(\\.(doc|pdf|xml|xls|xlsx|jpg|jpeg|gif|png|rar|zip|exe|bin|ppt|apk|"
            + "jar|mp3|aac|csv|json|eps|nc|fig)|/{3,}|#).*$";
    private final Set<String> urlSet;
    private final String url;
    private final String rootUrl;
    private final Site site;
    private Lemmatisator lemmatisator;
    private IndexService indexService;
    private LemmaService lemmaService;
    private PageService pageService;
    private SiteService siteService;
    private final PageRepository pageRepository;

    public UrlParser(String url, Site site, Set<String> urlSet, PageRepository pageRepository) {
        this.url = url.toLowerCase(Locale.ROOT);
        this.urlSet = urlSet;
        this.rootUrl = site.getUrl();
        this.site = site;
        this.pageRepository = pageRepository;
    }

    @Override
    protected void compute() {
        if (!siteService.isIndexingStopFlag()) {
            List<UrlParser> tasks = new LinkedList<>();
            try {
                Connection connection = Jsoup.connect(url).ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com");
                Thread.sleep(500);
                Connection.Response response = connection.execute();
                int responseCode = connection.response().statusCode();
                Document document = response.parse();
                Elements tagA = document.getElementsByTag("a");
                insertData(document, responseCode);
                for (Element element : tagA) {
                    String lowerCaseElementUrl = element.absUrl("href").toLowerCase(Locale.ROOT);
                    if (isUrlCorrect(lowerCaseElementUrl)) {
                        urlSet.add(lowerCaseElementUrl);
                        UrlParser subTask = new UrlParser(lowerCaseElementUrl, site, urlSet, pageRepository);
                        subTask.setIndexService(indexService);
                        subTask.setLemmaService(lemmaService);
                        subTask.setPageService(pageService);
                        subTask.setSiteService(siteService);
                        subTask.setLemmatisator(lemmatisator);
                        subTask.fork();
                        tasks.add(subTask);
                    }
                }
            } catch (IOException | InterruptedException | SQLException e) {
                siteService.updateErrorMessage(site, url + " - " + e.getMessage());
            }
            for (UrlParser parser : tasks) {
                parser.join();
            }
        }
    }

    public void insertData(Document document, int responseCode) throws IOException, SQLException, InterruptedException {
        siteService.updateStatusTime(site);
        Page page = pageService.createPageAndSave(url.substring(rootUrl.length()), responseCode,
                document.html(), site);
        String bodyText = document.body().text();
        String titleText =  document.title();

        if (responseCode == 200) {
            Map<String, Lemma> lemmaMap = lemmaService.createAndInsertLemmaOnDuplicateUpdateAndGetMap(site,
                    lemmatisator.getLemmaSet(bodyText + " " + titleText));
            HashMap<String, Float> titleLemmasCount = lemmatisator.getLemmasOnField(titleText);
            HashMap<String, Float> bodyLemmasCount = lemmatisator.getLemmasOnField(bodyText);
            HashMap<String, Float> lemmasAndRank = lemmatisator.calculateLemmasRank(lemmaMap, titleLemmasCount, bodyLemmasCount);
            indexService.createIndexAndSave(page, lemmasAndRank, lemmaMap, titleLemmasCount, bodyLemmasCount);
        }
    }

    private boolean isUrlCorrect(String url) {
        return url.startsWith(rootUrl) && !url.matches(urlRegex) && !urlSet.contains(url);
    }
}
