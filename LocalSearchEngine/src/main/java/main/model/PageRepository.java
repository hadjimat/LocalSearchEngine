package main.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends CrudRepository <Page, Integer> {

    @Query("select count(p) from Page p where p.siteBySiteId = ?1")
    long countBySiteBySiteId(Site siteBySiteId);

    @Query("select p from Page p where p.path = ?1 and p.siteBySiteId = ?2")
    Optional<Page> findByPathAndSiteBySiteId(String path, Site site);

    @Modifying
    @Query(value = "delete from _page " +
            "where site_id in (select id from _site where url = '?')", nativeQuery = true)
    void deleteBySiteId(String url);

    @Override
    @Modifying
    @Query("DELETE FROM Page")
    void deleteAll();
}
