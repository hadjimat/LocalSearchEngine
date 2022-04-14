package main.model;

import main.dto.interfaces.ModelId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO _lemma (frequency, lemma, site_id) " +
            "VALUES (?,?,?) ON DUPLICATE KEY UPDATE frequency = frequency + 1;", nativeQuery = true)
    void insertOnDuplicateUpdate(int frequency, String lemma, int site_id);

    Optional<Lemma> findLemmaByLemmaAndSiteBySiteId(String lemmaString, Site siteBySiteId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE _lemma set frequency = frequency - 1 " +
            "where id in (select lemma_id from indexes where page_id = ?);", nativeQuery = true)
    void unCountLemmasOfPage(int pageId);

    long countBySiteBySiteId(Site siteBySiteId);

    List<ModelId> findByLemmaInAndSiteBySiteIdOrderByFrequency(Collection<String> lemmas, Site site);

    @Override
    @Modifying
    @Query("DELETE FROM Lemma")
    void deleteAll();

    @Modifying
    @Query(value = "delete from _lemma " +
            "where site_id in (select id from _site where url = '?')", nativeQuery = true)
    void deleteLemmaBySiteId(String url);
}
