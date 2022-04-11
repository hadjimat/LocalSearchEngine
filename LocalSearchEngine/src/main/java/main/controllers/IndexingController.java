package main.controllers;

import lombok.RequiredArgsConstructor;
import main.SitesConfig;
import main.model.Field;
import main.model.FieldRepository;
import main.model.Site;
import main.responses.ErrorResponse;
import main.services.SiteService;
import main.services.UrlParser.UrlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class IndexingController {
    @Autowired
    private UrlParserService urlParserService;
    @Autowired
    private SitesConfig sitesConfig;
    @Autowired
    private SiteService siteService;
    @Autowired
    FieldRepository fieldRepository;

    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexing() {
        Field field1 = new Field();
        field1.setId(1);
        field1.setName("title");
        field1.setSelector("title");
        field1.setWeight(1);
        fieldRepository.save(field1);
        Field field = new Field();
        field.setId(2);
        field.setName("body");
        field.setWeight(0.8f);
        field.setSelector("body");
        fieldRepository.save(field);

        if (!siteService.isIndexingStarted()) {
            return ResponseEntity.ok(urlParserService.startIndexing());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация уже запущена"));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if (siteService.isIndexingStarted()) {
            return ResponseEntity.ok(urlParserService.stopIndexing());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация не запущена"));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> indexPage(@RequestParam(value = "url") String url) throws SQLException, IOException,
            InterruptedException {
        if (!siteService.isIndexingStarted()) {
            ArrayList<Site> siteArrayList = sitesConfig.getSites();
            for (Site siteFromConfig : siteArrayList) {
                if (url.toLowerCase(Locale.ROOT).contains(siteFromConfig.getUrl())) {
                    return ResponseEntity.ok(urlParserService.indexOnePage(url, siteFromConfig));
                }
            }
            return ResponseEntity.badRequest().body(new ErrorResponse("Данная страница находится за пределами сайтов, " +
                    "указаных в конфигурационном файле."));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse("Индексация уже запущена. Остановите индексацию, " +
                "или дождитесь ее окончания"));
    }
}
