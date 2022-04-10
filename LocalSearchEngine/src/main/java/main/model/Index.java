package main.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "indexes", schema = "search_engine")
public class Index {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false)
    private Page pageByPageId;

    @Column(name = "page_id", updatable = false, insertable = false)
    private int pageId;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id", nullable = false)
    private Field fieldByFieldId;

    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false)
    private Lemma lemmaByLemmaId;

    @Column(name = "lemma_id", updatable = false, insertable = false)
    private int lemmaId;

    @Column(name = "lemma_rank")
    private double lemmaRank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Index index = (Index) o;
        return id != 0 && Objects.equals(id, index.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
