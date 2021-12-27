package jpabasic.hellojpql.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED )
@DiscriminatorColumn(name = "type")
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id", nullable = false)
    private Long id;

    private String name;

}
