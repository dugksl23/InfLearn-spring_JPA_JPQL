package jpabasic.hellojpql.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SequenceGenerator(
        name = "Team_Sequence_Generator", // generator 이름
        sequenceName = "Team_Seq", // 매핑할 db 시퀀스 이름,
        initialValue = 1,
        allocationSize = 100
)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Team_Seq")
    @Column(name = "team_id", nullable = false)
    private Long id;

    @Column(name = "team_name")
    private String name;


    /**
     *   1. 다대일에서의 n + 1 문제 + @BatchSize()
     *   n이 되는 member 의 수가 무한정 늘어나기에, 초기 세팅된 회원의 수로 batchSize 를 조절하면
     *   in()로 batchsize만큼 묶어서 한번의 쿼리만 생성한다.
     *   그러나, 결국엔 초기 지정된 사이즈를 넘어갔을 경우에
     *   n개의 in()절이 발생한다. 결국 n+1을 batchSize 로 해결할 수 없다.
     */

    /**
     * 2. 일대다(collection)과 @BatchSize()
     * <p>
     * team 에서 각각의 연관관계를 갖는 member를 조회하려고 n개의 쿼리를 생성한다.
     * 이 문제를 해결하려고 fetch join 을 사용하였다.
     * 하지만, 이때 paging을 사용하려고 한다면...
     * collection 에서는 fetch join 과 paging api 함께 사용 할 수 없음
     * 그러나, 다대일에서는 n+1 이라서 연관관계가 어차피 하나라서 함께 사용 가능
     *
     * 어차피 페이징이 안 되기 때문에, 페이징의 기능으로서 @BatchSize() 를 사용
     *
     * @BatchSize는 는 in()로 묶어서 보낼 값을 지정하는 것이다.
     * ex) pagi ng max = 10
     * batchSize=2 -> in (19,20) x 5 번 쿼리 생성
     * ex) paging max = 10
     * size=5 -> in (19,20,21,22,23) x 2번 쿼리 생성
     * <p>
     *
     */
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();


    @Builder
    public Team(String name) {
        this.name = name;
    }

}
