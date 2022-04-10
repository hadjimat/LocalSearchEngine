package main.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.model.SiteStatusType;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class DetailedStatisticsDto {
    private String url;
    private String name;
    private SiteStatusType status;
    private Timestamp statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
