package main.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends CrudRepository <Page, Integer> {

    long countBySiteBySiteId(Site siteBySiteId);

    Optional<Page> findByPathAndSiteBySiteId(String path, Site site);

    @Override
    @Modifying
    @Query("DELETE FROM Page")
    void deleteAll();
}
