package main.services;

import lombok.Getter;
import lombok.Setter;
import main.model.Site;
import main.model.SiteRepository;
import main.model.SiteStatusType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class SiteService {

    private final SiteRepository siteRepository;

    @Getter
    @Setter
    private boolean isIndexingStarted;

    @Getter
    @Setter
    private boolean indexingStopFlag;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<Site> findAllSites() {
        return siteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Site findSiteByName(String siteName) {
        return siteRepository.findByUrl(siteName).orElseThrow();
    }

    @Transactional
    public Site saveSiteIfNotExist(Site site) {
        Optional<Site> siteOptional = siteRepository.findByName(site.getName());
        return siteOptional.orElseGet(() -> siteRepository.save(site));
    }

    public void updateStatusTime(Site site) {
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        siteRepository.save(site);
    }

    @Transactional
    public void updateStatus(Site site, SiteStatusType statusType) {
        site.setStatus(statusType);
        siteRepository.save(site);
    }

    @Transactional
    public void updateErrorMessage(Site site, String error) {
        site.setLastError(error);
        siteRepository.save(site);
    }

    @Transactional
    public void deleteAllSiteData() {
        siteRepository.deleteAll();
    }

    @Transactional
    public void deleteSiteData(String url) {
    }
}
