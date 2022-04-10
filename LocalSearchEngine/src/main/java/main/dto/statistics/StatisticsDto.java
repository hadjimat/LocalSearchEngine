package main.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatisticsDto {
    private TotalStatisticsDto total;
    private List<DetailedStatisticsDto> detailed;
}
