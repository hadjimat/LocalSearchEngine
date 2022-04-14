package main.model;

import lombok.*;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "_site", schema = "search_engine")
public class Site {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "enum")
    private SiteStatusType status;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "siteBySiteId")
    @ToString.Exclude
    private Collection<Lemma> lemmataById;

    @OneToMany(mappedBy = "siteBySiteId")
    @ToString.Exclude
    private Collection<Page> pagesById;

}
