package jpabasic.hellojpql.test;


import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Product;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.BookRepository;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.ProductRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JpaJoinTest {


    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BookRepository bookRepository;

    @PersistenceContext
    private EntityManager em;

    // join - inner, outer / 연관관계 반드시 필요함.
    // seta - 두 테이블 나래비, 연관관계 필요없음
    // join on - 테이블 연관관계 필요없음.

    @Test
    void innerJoinTest() { // cross join 을 통해 intersection(교집합)

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("member" + i);
            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m from Member m join m.team where m.team.name = :name order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .setParameter("name", "team1") // 이름 기반, 중복 가
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId());
        });

        List<Member> membersJoinTeam = memberRepository.findMembersJoinTeam();
        membersJoinTeam.stream().forEach(result -> {
            System.out.println("member : " + result.getId());
        });

    }

    @Test
    void outerJoinTest() { // 두 테이블 중 조인 대상 테이블을 기준으로 데이터를 뽑는다.

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("member" + i);
            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m from Member m left join m.team where m.team.name = :name order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .setParameter("name", "team1") // 이름 기반, 중복 가
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId());
        });

        List<Member> membersJoinTeam = memberRepository.findMembersJoinTeam();
        membersJoinTeam.stream().forEach(result -> {
            System.out.println("member : " + result.getId());
        });

    }


    @Test
    void setaJoinTest() { // seta join은 inner join을 바탕으로 하며, 두 테이블 모두 필터링의 대상이 된다.

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("member" + i);
            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m from Member m, Product t where m.team.id = t.id order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId());
        });
    }

    @Test
    void corssJoinTest() { // 두 테이블 모두 필터링의 대상이 된다.

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("team" + i);

            // 외부 조인용 엔티티
            Product product = new Product();
            product.setName("team" + i);
            product.setPrice(1000);
            product.setStockQuantity(1);
            productRepository.save(product);

            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m from Member m join m.team order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId());
        });
    }

    @Test
    void joinWithoutRelationTest() { // 연관관계가 없는 두 테이블의 join

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("team" + i);

            // 외부 조인용 엔티티
            Product product = new Product();
            product.setName("team" + i);
            product.setPrice(1000);
            product.setStockQuantity(1);
            productRepository.save(product);

            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m from Member m join Product p order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId());
        });
    }

    @Test
    void JoinOnTest() {

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("team" + i);

            // 외부 조인용 엔티티
            Product product = new Product();
            product.setName("team" + i);
            product.setPrice(1000);
            product.setStockQuantity(1);
            productRepository.save(product);

            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        String query = "select m, p from Member m join Product p on m.name  = p.name and m.name = :name order by m.id desc";

        List<Object[]> resultList = em.createQuery(query)
                .setParameter("name", "team1")
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        Object[] objects = resultList.get(0);
        Arrays.stream(objects).filter(type -> type instanceof Member).forEach(member -> {
            System.out.println(((Member) member).getName());
            System.out.println(((Member) member).getAge());

        });
        Arrays.stream(objects).filter(type -> type instanceof Product).forEach(product -> {
            System.out.println(((Product) product).getName());
        });

    }

}
