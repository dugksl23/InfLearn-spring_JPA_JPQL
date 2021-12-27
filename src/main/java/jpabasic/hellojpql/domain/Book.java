package jpabasic.hellojpql.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "Book")
public class Book extends Item {


}
