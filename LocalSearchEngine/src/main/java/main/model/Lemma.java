package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "lemmas")
@NoArgsConstructor
public class Lemma {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "lemma")
    private String lemma;

    @Column(name = "frequency")
    private int frequency;

    @OneToMany(mappedBy = "lemmaByLemmaId")
    @ToString.Exclude
    private Collection<Index> indicesById;

    @ManyToOne()
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site siteBySiteId;

    public Lemma(String lemma, int frequency, Site siteBySiteId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.siteBySiteId = siteBySiteId;
    }
}
