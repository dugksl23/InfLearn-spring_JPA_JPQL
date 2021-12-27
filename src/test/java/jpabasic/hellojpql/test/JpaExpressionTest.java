package jpabasic.hellojpql.test;


import jpabasic.hellojpql.domain.*;
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
public class JpaExpressionTest {


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

    @Test
    void typeExpressionTest() {

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("team" + i);
            member.setMemberType(MemberType.ADMIN);

            member.entryTeam(team1); //객체 연관관계 매핑 ( ORM)
            memberRepository.save(member); // DB 연관관계 매핑 (RDB)
        }

        // Enum 풀 패키지 경로
        List<Object[]> list = em.createQuery("select m.memberType, 'hello', 10, 10l, 10F, 10D,true from Member m where m.memberType = jpabasic.hellojpql.domain.MemberType.ADMIN").getResultList();
        Object[] objects = list.get(0);
        Arrays.stream(objects).forEach(System.out::println);

        // Enum 이름 기반
        List<Object[]> list2 = em.createQuery("select m.memberType, 'hello', 10, 10l, 10F, 10D, true from Member m where m.memberType = :type")
                .setParameter("type", MemberType.ADMIN)
                .getResultList();


    }

    @Test
    void typeExpressionWithEntity상속관계Test() {


        Book book = new Book();
        book.setName("ddd");
        // 상속관계용 Book SAVE
        Item save = bookRepository.save(book);
        System.out.println("name " + save.getName());

        List<Item> resultList = em.createQuery("select i from Item i where type(i) = jpabasic.hellojpql.domain.Book", Item.class).getResultList();
        resultList.stream().forEach(result -> {
            Book book1 = (Book) result;
            System.out.println(book1.getId());
            System.out.println(book1.getName());
        });

    }

    @Test
    void conditionExpressionTest() {

        Member member = new Member();
        member.setMemberType(MemberType.USER);
        member.setName("ADMIN");

        Member member1 = new Member();
        member.setName("111");
        member.setMemberType(MemberType.ADMIN);
        memberRepository.saveAll(Arrays.asList(member1, member));


        /**
         * 1. coalesce()
         * @return null 이 아닌 것들을 모두 반환, null 이면 지정된 default value 반환.
         */
        List<String> resultList1 = em.createQuery("select coalesce(m.name, '이름없는 회원') from Member m", String.class).getResultList();
        resultList1.stream().forEach(member2 -> {
            System.out.println("member name : " + member2);
        });

        /**
         * 2. nullIf(A,B)
         * @return A와 B가 같으면, Null 반환. 다르면 첫번째 값을 반환
         */
        List<String> resultList2 = em.createQuery("select nullif(m.name, 'ADMIN') from Member m").getResultList();
        resultList2.stream().forEach(member2 -> {
            System.out.println("member type : " + member2);
        });

    }
}
