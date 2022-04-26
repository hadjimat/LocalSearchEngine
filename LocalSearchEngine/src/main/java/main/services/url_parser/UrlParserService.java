package main.services.url_parser;

import main.lemmatisator.Lemmatisator;
import main.config.SitesConfig;
import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
import main.model.SiteStatusType;
import main.responses.ResultResponse;
import main.services.IndexService;
import main.services.LemmaService;
import main.services.PageService;
import main.services.SiteService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
public class UrlParserService {

    private final SitesConfig sitesConfig;
    private final IndexService indexService;
    private final LemmaService lemmaService;
    private final PageService pageService;
    private final SiteService siteService;
    private final PageRepository pageRepository;

    public UrlParserService(SitesConfig sitesConfig, IndexService indexService, LemmaService lemmaService, PageService pageService, SiteService siteService, PageRepository pageRepository) {
        this.sitesConfig = sitesConfig;
        this.indexService = indexService;
        this.lemmaService = lemmaService;
        this.pageService = pageService;
        this.siteService = siteService;
        this.pageRepository = pageRepository;
    }

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

    public Object stopIndexing() {
        siteService.setIndexingStarted(false);
        siteService.setIndexingStopFlag(true);
        return new ResultResponse();
    }

    void asyncStartIndexing(Site siteFromConfig) throws IOException {
        siteFromConfig.setStatusTime(new Timestamp(System.currentTimeMillis()));
        Site dbSite = siteService.saveSiteIfNotExist(siteFromConfig);
        siteFromConfig.setStatus(SiteStatusType.INDEXING);
        dbSite.setStatus(SiteStatusType.INDEXING);

        startUrlParser(dbSite);

        if (siteService.isIndexingStopFlag()) {
            siteService.updateStatus(dbSite, SiteStatusType.FAILED);
            siteService.updateErrorMessage(dbSite, "Indexing Stopped");
        } else {
            siteService.updateStatus(dbSite, SiteStatusType.INDEXED);
        }
        siteService.setIndexingStarted(false);
    }

    public void startIndexingOnePage(String url, Site siteFromConfig) throws IOException {
        siteFromConfig.setStatusTime(new Timestamp(System.currentTimeMillis()));
        siteFromConfig.setStatus(SiteStatusType.INDEXING);
        Site dbSite = siteService.saveSiteIfNotExist(siteFromConfig);

        Optional<Page> pageOptional = pageService.getPageByPath(url.substring(dbSite.getUrl().length()), dbSite);
        if (pageOptional.isPresent()) {
            lemmaService.unCountLemmasOfPage(pageOptional.get().getId());
            pageService.deletePage(pageOptional.get());
        }

        startUrlParser(dbSite);

        if (siteService.isIndexingStopFlag()) {
            siteService.updateStatus(dbSite, SiteStatusType.FAILED);
            siteService.updateErrorMessage(dbSite, "Indexing Stopped");
        } else {
            siteService.updateStatus(dbSite, SiteStatusType.INDEXED);
        }
        siteService.setIndexingStarted(false);
    }

    private void startUrlParser(Site dbSite) throws IOException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Set<String> parsedURLs = Collections.synchronizedSet(new HashSet<>());
        parsedURLs.add(dbSite.getUrl().toLowerCase(Locale.ROOT) + "/");
        UrlParser urlParser = new UrlParser(dbSite.getUrl().toLowerCase(Locale.ROOT) + "/", dbSite, parsedURLs, pageRepository);
        urlParser.setIndexService(indexService);
        urlParser.setLemmaService(lemmaService);
        urlParser.setPageService(pageService);
        urlParser.setSiteService(siteService);
        urlParser.setLemmatisator(new Lemmatisator());
        forkJoinPool.invoke(urlParser);
    }
}
