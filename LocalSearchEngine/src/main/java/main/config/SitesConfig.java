package main.config;

import lombok.Getter;
import lombok.Setter;
import main.model.Site;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@ConfigurationProperties(prefix = "siteconfig")
@Getter
@Setter
public class SitesConfig {
    private ArrayList<Site> sites;
}
