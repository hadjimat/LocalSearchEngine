package main.controllers;

import lombok.RequiredArgsConstructor;
import main.SitesConfig;
import main.model.FieldRepository;
import main.model.Site;
import main.responses.ErrorResponse;
import main.services.SiteService;
import main.services.url_parser.UrlParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class IndexingController {

    private final UrlParserService urlParserService;
    private final SitesConfig sitesConfig;
    private final SiteService siteService;
    private final FieldRepository fieldRepository;

    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexing() {
        if (!siteService.isIndexingStarted()) {
            return ResponseEntity.ok(urlParserService.startIndexing());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация запущена"));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if (siteService.isIndexingStarted()) {
            return ResponseEntity.ok(urlParserService.stopIndexing());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация не запущена"));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> indexPage(@RequestParam(value = "url") String url) throws IOException {
        if (!siteService.isIndexingStarted()) {
            ArrayList<Site> siteArrayList = sitesConfig.getSites();
            for (Site siteFromConfig : siteArrayList) {
                if (url.toLowerCase(Locale.ROOT).contains(siteFromConfig.getUrl())) {
                    urlParserService.startIndexingOnePage(url, siteFromConfig);
                    return ResponseEntity.status(HttpStatus.OK).body("ok");
                }
            }
            return ResponseEntity.badRequest().body(new ErrorResponse("Данная страница находится за пределами сайтов, " +
                    "указаных в конфигурационном файле."));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация запущена"));
    }
}
