package main.services;

import main.dto.interfaces.ModelId;
import main.model.Lemma;
import main.model.LemmaRepository;
import main.model.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LemmaService {
    @Autowired
    private LemmaRepository lemmaRepository;

    public HashMap<String, Lemma> createAndInsertLemmaOnDuplicateUpdateAndGetMap(Site site, Set<String> lemmaSet) {
        HashMap<String, Lemma> lemmaMap = new HashMap<>();
        for (String lemmaString : lemmaSet) {
            lemmaRepository.insertOnDuplicateUpdate(1, lemmaString, site.getId());
            Optional<Lemma> optionalLemma = lemmaRepository.findLemmaByLemmaAndSiteBySiteId(lemmaString, site);
            optionalLemma.ifPresent(lemma -> lemmaMap.put(lemma.getLemma(), lemma));
        }

        return lemmaMap;
    }

    @Transactional
    public void unCountLemmasOfPage(int pageId) {
        lemmaRepository.unCountLemmasOfPage(pageId);
    }

    @Transactional(readOnly = true)
    public List<ModelId> findLemmasIdBySiteOrderByFrequency(Set<String> lemmas, Site site) {
        return lemmaRepository.findByLemmaInAndSiteBySiteIdOrderByFrequency(lemmas, site);
    }

    @Transactional
    public void deleteAllLemmaData(){
        lemmaRepository.deleteAll();
    }

    @Transactional
    public void deleteLemmaBySiteId(String url){
        lemmaRepository.deleteLemmaBySiteId(url);
    }
}
