package main.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Data
@Table(name = "sites", schema = "search_engine")
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
    private Collection<Lemma> lemmataById;

    @OneToMany(mappedBy = "siteBySiteId")
    private Collection<Page> pagesById;
}
