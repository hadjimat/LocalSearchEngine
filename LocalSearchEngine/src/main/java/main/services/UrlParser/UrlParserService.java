package main.services.UrlParser;

import main.Lemmatisator.Lemmatisator;
import main.SitesConfig;
import main.model.Site;
import main.model.SiteStatusType;
import main.responses.ResultResponse;
import main.services.IndexService;
import main.services.LemmaService;
import main.services.PageService;
import main.services.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
public class UrlParserService {
    @Autowired
    private SitesConfig sitesConfig;
    @Autowired
    private IndexService indexService;
    @Autowired
    private LemmaService lemmaService;
    @Autowired
    private PageService pageService;
    @Autowired
    private SiteService siteService;

    public Object startIndexing() {
        ArrayList<Site> sitesConfigSites = sitesConfig.getSites();
        siteService.setIndexingStarted(true);
        siteService.setIndexingStopFlag(false);

        pageService.deleteAllPage();
        lemmaService.deleteAllLemmaData();
        siteService.deleteAllSiteData();

        for (Site site : sitesConfigSites) {
            CompletableFuture.runAsync(() -> {
                try {
                    asyncStartIndexing(site);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, ForkJoinPool.commonPool());
        }
        return new ResultResponse();
    }

     void asyncStartIndexing(Site siteFromConfig) throws IOException {
        asyncStartIndexing(null, siteFromConfig);
    }

    public Object stopIndexing() {
        siteService.setIndexingStarted(false);
        siteService.setIndexingStopFlag(true);
        return new ResultResponse();
    }

     void asyncStartIndexing(String url, Site siteFromConfig) throws IOException {

         siteFromConfig.setStatusTime(new Timestamp(System.currentTimeMillis()));
         siteFromConfig.setStatus(SiteStatusType.INDEXING);
         System.out.println("s1");
         Site dbSite = siteService.saveSiteIfNotExist(siteFromConfig);
         dbSite.setStatus(SiteStatusType.INDEXING);
         if (url == null) {
             ForkJoinPool forkJoinPool = new ForkJoinPool();
             Set<String> parsedURLs = Collections.synchronizedSet(new HashSet<>());
             System.out.println("s2");
             parsedURLs.add(dbSite.getUrl().toLowerCase(Locale.ROOT) + "/");
             UrlParser urlParser = new UrlParser(dbSite.getUrl().toLowerCase(Locale.ROOT) + "/", dbSite, parsedURLs);
             urlParser.setIndexService(indexService);
             urlParser.setLemmaService(lemmaService);
             urlParser.setPageService(pageService);
             urlParser.setSiteService(siteService);
             urlParser.setLemmatisator(new Lemmatisator());
             System.out.println("s3");
             forkJoinPool.invoke(urlParser);
             System.out.println("loading");
             if (siteService.isIndexingStopFlag()) {
                 siteService.updateStatus(dbSite, SiteStatusType.FAILED);
                 siteService.updateErrorMessage(dbSite, "Indexing Stopped");
             } else {
                 siteService.updateStatus(dbSite, SiteStatusType.INDEXED);
             }
             siteService.setIndexingStarted(false);
         } else {
             ForkJoinPool forkJoinPool = new ForkJoinPool();
             Set<String> parsedURLs = Collections.synchronizedSet(new HashSet<>());
             System.out.println("s2");
             parsedURLs.add(dbSite.getUrl().toLowerCase(Locale.ROOT) + "/");
             UrlParser urlParser = new UrlParser(url + "/", dbSite, parsedURLs);
             urlParser.setIndexService(indexService);
             urlParser.setLemmaService(lemmaService);
             urlParser.setPageService(pageService);
             urlParser.setSiteService(siteService);
             urlParser.setLemmatisator(new Lemmatisator());
             System.out.println("s3");
             forkJoinPool.invoke(urlParser);
             System.out.println("loading");
             if (siteService.isIndexingStopFlag()) {
                 siteService.updateStatus(dbSite, SiteStatusType.FAILED);
                 siteService.updateErrorMessage(dbSite, "Indexing Stopped");
             } else {
                 siteService.updateStatus(dbSite, SiteStatusType.INDEXED);
             }
             siteService.setIndexingStarted(false);
         }
     }

//        siteFromConfig.setStatusTime(new Timestamp(System.currentTimeMillis()));
//        siteFromConfig.setStatus(SiteStatusType.INDEXING);
//        Site dbSite = siteService.saveSiteIfNotExist(siteFromConfig);
//
//        Optional<Page> pageOptional = pageService.getPageByPath(url.substring(dbSite.getUrl().length()), dbSite);
//        if (pageOptional.isPresent()) {
//            lemmaService.unCountLemmasOfPage(pageOptional.get().getId());
//            pageService.deletePage(pageOptional.get());
//        }
//
//        Set<String> parsedURLs = Collections.synchronizedSet(new HashSet<>());
//        parsedURLs.add(dbSite.getUrl().toLowerCase(Locale.ROOT));
//        UrlParser urlParser = new UrlParser(url.toLowerCase(Locale.ROOT), dbSite, parsedURLs);
//        urlParser.setIndexService(indexService);
//        urlParser.setLemmaService(lemmaService);
//        urlParser.setPageService(pageService);
//        urlParser.setSiteService(siteService);
//        urlParser.setLemmatisator(new Lemmatisator());
//        Connection connection = Jsoup.connect(url).userAgent("searchEngineBot/0.1").referrer("http://www.google.com")
//                .ignoreHttpErrors(true).ignoreContentType(true);
//        Document document = connection.get();
//        int response = connection.response().statusCode();
//        urlParser.insertData(document, response);
//        siteService.updateStatus(dbSite, SiteStatusType.INDEXING);
//        return new ResultResponse();


    public Object startIndexingOnePage(String url) {
        ArrayList<Site> sitesConfigSites = sitesConfig.getSites();
        siteService.setIndexingStarted(true);
        siteService.setIndexingStopFlag(false);

        pageService.deleteAllPage();
        lemmaService.deleteAllLemmaData();
        siteService.deleteAllSiteData();

        for (Site site : sitesConfigSites) {
            CompletableFuture.runAsync(() -> {
                try {
                    asyncStartIndexing(url,site);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, ForkJoinPool.commonPool());
        }
        return new ResultResponse();
    }
}
