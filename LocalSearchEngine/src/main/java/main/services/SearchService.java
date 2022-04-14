package main.services;

import main.Lemmatisator.Lemmatisator;
import main.dto.interfaces.IndexPageId;
import main.dto.interfaces.ModelId;
import main.dto.interfaces.PageRelevanceAndData;
import main.dto.search.PageSearchDto;
import main.responses.SearchResponse;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
public class SearchService {
    private final Lemmatisator lemmatisator;
    @Autowired
    LemmaService lemmaService;
    @Autowired
    IndexService indexService;
    @Autowired
    SiteService siteService;

    public SearchService() throws IOException {
        this.lemmatisator = new Lemmatisator();
    }

    public Object search(String findQuery, String siteUrl, int offset, int limit) {
        Set<String> findQueryLemmas = lemmatisator.getLemmaSet(findQuery);
        Set<ModelId> allLemmasIds = new HashSet<>();
        Set<IndexPageId> allPageIds = new HashSet<>();

        if (siteUrl == null) {
            siteService.findAllSites().forEach(site -> {
                List<ModelId> lemmasIdsOfSite = lemmaService.findLemmasIdBySiteOrderByFrequency(findQueryLemmas, site);
                allLemmasIds.addAll(lemmasIdsOfSite);
                allPageIds.addAll(getPageIdsOfSite(lemmasIdsOfSite));
            });

        } else {
            List<ModelId> lemmasIdsOfSite = lemmaService.findLemmasIdBySiteOrderByFrequency(findQueryLemmas,
                    siteService.findSiteByName(siteUrl));
            allLemmasIds.addAll(lemmasIdsOfSite);
            allPageIds.addAll(getPageIdsOfSite(lemmasIdsOfSite));
        }

        List<PageRelevanceAndData> pageData = indexService.findPageRelevanceAndData(allPageIds, allLemmasIds,
                limit, offset);

        return new SearchResponse(allPageIds.size(), createSearchResult(pageData, findQuery));
    }

    private List<IndexPageId> getPageIdsOfSite(@NotNull List<ModelId> lemmasIdsOfSite) {
        List<IndexPageId> pageIdsOfSite = new ArrayList<>();
        if (!lemmasIdsOfSite.isEmpty()) {
            pageIdsOfSite = indexService.findPagesIds(lemmasIdsOfSite.get(0).getId());
            if (lemmasIdsOfSite.size() > 2) {
                for (int lemma = 1; lemma < lemmasIdsOfSite.size() - 1; lemma++) {
                    pageIdsOfSite = indexService.getPagesIdOfNextLemmas(lemmasIdsOfSite.get(lemma).getId(),
                            pageIdsOfSite);
                }
                return pageIdsOfSite;
            }
        }
        return pageIdsOfSite;
    }

    private ArrayList<PageSearchDto> createSearchResult(List<PageRelevanceAndData> pageData, String findQuery) {
        ArrayList<PageSearchDto> searchResult = new ArrayList<>();

        pageData.forEach(pageRelevanceAndData -> {
            PageSearchDto searchDto = new PageSearchDto();
            searchDto.setSite(pageRelevanceAndData.getSite());
            searchDto.setSiteName(pageRelevanceAndData.getSiteName());
            searchDto.setUri(pageRelevanceAndData.getUri());
            searchDto.setTitle(Jsoup.parse(pageRelevanceAndData.getContent()).title());
            searchDto.setSnippet(getSnippetInHtml(pageRelevanceAndData.getContent(), findQuery));
            searchDto.setRelevance(pageRelevanceAndData.getRelevance());
            searchResult.add(searchDto);
        });
        System.out.println(searchResult);
        return searchResult;
    }
    public static String getSnippetInHtml(String htmlText, String searchQuery) {
        Document doc = Jsoup.parse(htmlText);
        String textOfSearchQuery = doc.getElementsContainingOwnText(searchQuery).text();
        String[] queryWords = searchQuery.split("\\s+");

        if (!textOfSearchQuery.isEmpty()) {
            int firstIndexOfSnippet = textOfSearchQuery.indexOf(searchQuery) > 80 ?
                    textOfSearchQuery.indexOf(searchQuery) - 80 : 0;
            int lastIndexOfSnippet = Math.min(firstIndexOfSnippet + searchQuery.length() + 160,
                    textOfSearchQuery.length());

            String firstWordSnippet = textOfSearchQuery.substring(firstIndexOfSnippet, lastIndexOfSnippet)
                    .replaceAll(createSnippetRegex(queryWords[0]), "<b>" + queryWords[0]);

            return firstWordSnippet.replaceAll(createSnippetRegex(queryWords[queryWords.length - 1]),
                    queryWords[queryWords.length - 1] + "</b>");
        } else {
            StringBuilder snippetBuilder = new StringBuilder();

            for (String word : queryWords) {
                String substring = word.substring(0, word.length() - 2);
                String textOfSearchWord = doc.getElementsContainingOwnText(substring).text();
                if (!textOfSearchWord.isEmpty()) {
                    int firstIndexOfSnippet = textOfSearchWord.indexOf(word) > 30 ?
                            textOfSearchWord.indexOf(word) - 30 : 0;
                    int lastIndexOfSnippet = Math.min(firstIndexOfSnippet + word.length() + 80, textOfSearchWord.length() - 1);
                    String snippetPart = textOfSearchWord.substring(firstIndexOfSnippet, lastIndexOfSnippet)
                            .replaceAll(createSnippetRegex(substring), "<b>" + substring + "</b>");
                    snippetBuilder.append(snippetPart);
                    snippetBuilder.append("...");
                }
            }

            return snippetBuilder.toString();
        }
    }

    private static String createSnippetRegex(String word) {
        String firstChar = String.valueOf(word.charAt(0));
        return "(?i)([" + firstChar.toLowerCase(Locale.ROOT) + firstChar.toUpperCase(Locale.ROOT) + "]" +
                word.substring(1) + ")";
    }
}

