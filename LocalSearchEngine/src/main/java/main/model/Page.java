package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "Pages" )
@NoArgsConstructor
public class Page {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "path")
    private String path;

    @Column(name = "code")
    private int statusCode;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT NOT NULL")
    private String content;

    @OneToMany(mappedBy = "pageByPageId")
    private Collection<Index> indicesById;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site siteBySiteId;

    public Page(String path, int statusCode, String content, Site siteBySiteId) {
        this.path = path;
        this.statusCode = statusCode;
        this.content = content;
        this.siteBySiteId = siteBySiteId;
    }
}
