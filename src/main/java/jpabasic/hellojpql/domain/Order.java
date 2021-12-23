package jpabasic.hellojpql.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    // 주문 총 상세 정보
    // - 하나의 주문에 하나의 제품만 결제될 경우
    // - 오더와 제품의 N:1

    @Id @GeneratedValue
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "order_total_price")
    private Integer orderPrice;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productList;


}
