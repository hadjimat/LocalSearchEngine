package main.controllers;

import lombok.RequiredArgsConstructor;
import main.responses.ErrorResponse;
import main.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "site", required = false) String site,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "limit", defaultValue = "50", required = false) int limit)
            throws SQLException {
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Задан пустой поисковый запрос"));
        }
        return ResponseEntity.ok(searchService.search(query, site, offset, limit));
    }
}
