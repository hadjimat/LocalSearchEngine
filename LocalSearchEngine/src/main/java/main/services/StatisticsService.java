package main.services;

import main.dto.statistics.DetailedStatisticsDto;
import main.dto.statistics.StatisticsDto;
import main.dto.statistics.TotalStatisticsDto;
import main.model.LemmaRepository;
import main.model.PageRepository;
import main.model.SiteRepository;
import main.responses.StatisticResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    public StatisticsService(SiteRepository siteRepository, PageRepository pageRepository, LemmaRepository lemmaRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
    }


    public StatisticResponse getStatistics() {
        TotalStatisticsDto totalStatisticsDto = new TotalStatisticsDto(siteRepository.count(), pageRepository.count(),
                lemmaRepository.count(), true);
        List<DetailedStatisticsDto> detailedStatisticsDtoList = new ArrayList<>();
        siteRepository.findAll().forEach(site -> {
            DetailedStatisticsDto detailedStatisticsDto = new DetailedStatisticsDto(site.getUrl(), site.getName(),
                    site.getStatus(), site.getStatusTime(), site.getLastError(),
                    pageRepository.countBySiteBySiteId(site), lemmaRepository.countBySiteBySiteId(site));
            detailedStatisticsDtoList.add(detailedStatisticsDto);
        });
        return new StatisticResponse(new StatisticsDto(totalStatisticsDto, detailedStatisticsDtoList));
    }

}
