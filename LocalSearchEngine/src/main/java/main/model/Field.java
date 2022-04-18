package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "_field")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String selector;

    @Column(nullable = false)
    float weight;

    @OneToMany(mappedBy = "fieldByFieldId")
    private Collection<Index> indexesById;
}
