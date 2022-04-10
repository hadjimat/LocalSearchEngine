package main.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import main.dto.statistics.StatisticsDto;

@Data
@RequiredArgsConstructor
public class StatisticResponse {
    private String result = "true";

    private StatisticsDto statistics;

    public StatisticResponse(StatisticsDto statisticsDto) {
        this.statistics = statisticsDto;
    }
}
