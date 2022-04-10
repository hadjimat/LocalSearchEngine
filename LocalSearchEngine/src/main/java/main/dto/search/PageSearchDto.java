package main.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSearchDto implements Comparable<PageSearchDto> {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;


    @Override
    public int compareTo(PageSearchDto o) {
        return Float.compare(o.relevance, this.relevance);
    }
}
