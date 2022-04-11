package main.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "_index", schema = "search_engine")
public class Index {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (page_id) REFERENCES _page(id) ON DELETE CASCADE"))
    private Page pageByPageId;

    @Column(name = "page_id", updatable = false, insertable = false)
    private int pageId;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (field_id) REFERENCES _field(id) ON DELETE CASCADE"))
    private Field fieldByFieldId;

    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (lemma_id) REFERENCES _lemma(id) ON DELETE CASCADE"))
    private Lemma lemmaByLemmaId;

    @Column(name = "lemma_id", updatable = false, insertable = false)

    private int lemmaId;

    @Column(name = "lemma_rank")
    private double lemmaRank;

}
