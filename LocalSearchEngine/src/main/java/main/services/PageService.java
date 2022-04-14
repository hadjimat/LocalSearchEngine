package main.services;

import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private PageRepository pageRepository;

    public Page createPageAndSave(String path, int code, String content, Site site) {
        Page page = new Page(path, code, content, site);
        pageRepository.save(page);
        return page;
    }

    @Transactional
    public Optional<Page> getPageByPath(String path, Site site) {
        return pageRepository.findByPathAndSiteBySiteId(path, site);
    }

    @Transactional
    public void deletePage(Page page) {
        pageRepository.delete(page);
    }

    @Transactional
    public void deleteAllPage(){
        pageRepository.deleteAll();
    }

    @Transactional
    public void  deletePage(String url){
        pageRepository.deleteBySiteId(url);

    }

}
