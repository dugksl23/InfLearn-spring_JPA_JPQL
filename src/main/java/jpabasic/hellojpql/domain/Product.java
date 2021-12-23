package jpabasic.hellojpql.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(name = "product_name ")
    private String name;
    private Integer stockQuantity;
    @Column(name = "stock_amount")
    private Integer price;
}
