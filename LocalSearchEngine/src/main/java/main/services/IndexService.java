package main.services;

import main.dto.interfaces.IndexPageId;
import main.dto.interfaces.ModelId;
import main.dto.interfaces.PageRelevanceAndData;
import main.model.Index;
import main.model.IndexRepository;
import main.model.Lemma;
import main.model.Page;
import main.requests.OffsetAndLimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class IndexService {
    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private FieldService fieldService;

    public void addIndex(Index index) {
        indexRepository.save(index);
    }

    public void createIndexAndSave(Page page, Map<String, Float> lemmasAndRank,
                                   Map<String, Lemma> lemmas,
                                   HashMap<String, Float> titleLemmas,
                                   HashMap<String, Float> bodyLemmas) {
        createIndex(page, lemmasAndRank, lemmas, titleLemmas, 1);
        createIndex(page, lemmasAndRank, lemmas, bodyLemmas, 2);
    }

    private void createIndex(Page page,
                             Map<String, Float> lemmasAndRank,
                             Map<String, Lemma> lemmas,
                             HashMap<String, Float> lemmasOnField, int fieldId) {
        for (Map.Entry<String, Float> lemma : lemmasOnField.entrySet()) {
            Index index = new Index();
            index.setPageByPageId(page);
            if (fieldService.getById(fieldId).isPresent()){
            index.setFieldByFieldId(fieldService.getById(fieldId).get());
            }
//                    .orElseThrow(() -> new NullPointerException("field " + fieldId + " Not Found")));
            index.setLemmaByLemmaId(lemmas.get(lemma.getKey()));
            index.setLemmaRank(lemmasAndRank.get(lemma.getKey()));

            addIndex(index);
        }
    }
    @Transactional(readOnly = true)
    public List<IndexPageId> findPagesIds(int lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }

    @Transactional(readOnly = true)
    public List<IndexPageId> getPagesIdOfNextLemmas(int lemmaId, List<IndexPageId> pageIdList) {
        ArrayList<Integer> pageIds = new ArrayList<>();
        pageIdList.forEach(indexPageId -> pageIds.add(indexPageId.getPageId()));
        return indexRepository.findByLemmaIdAndPageIdIn(lemmaId, pageIds);
    }

    @Transactional(readOnly = true)
    public List<PageRelevanceAndData> findPageRelevanceAndData(Set<IndexPageId> pageIdList, Set<ModelId> lemmaIdList,
                                                               int limit, int offset) {
        ArrayList<Integer> pageIds = new ArrayList<>();
        pageIdList.forEach(indexPageId -> pageIds.add(indexPageId.getPageId()));
        ArrayList<Integer> lemmaIds = new ArrayList<>();
        lemmaIdList.forEach(indexLemmaId -> lemmaIds.add(indexLemmaId.getId()));

        return indexRepository.findPageRelevanceAndData(pageIds, lemmaIds, new OffsetAndLimitRequest(limit, offset));
    }

    @Transactional
    public void deleteAllIndexData(){
        indexRepository.deleteAll();
    }
}


