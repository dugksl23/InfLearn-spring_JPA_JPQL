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
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
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

        String query = "select nullif(m.name, 'ADMIN') from Member m";
        String query2 = "select m from Member m";
        List<String> resultList2 = em.createQuery("select nullif(m.name, 'ADMIN') from Member m").getResultList();
        resultList2.stream().forEach(member2 -> {
            System.out.println("member type : " + member2);
        });

    }

    @Test
    @Transactional
    void jpqlFunctionTest() {

        Member member = new Member();
        member.setMemberType(MemberType.USER);
        member.setName("ADMIN");

        Member member1 = new Member();
        member.setName("111");
        member.setMemberType(MemberType.ADMIN);
        memberRepository.saveAll(Arrays.asList(member1, member));


        /**
         * 1. concat(A,B,C, etc...)
         * @return 매개변수로 등록된 문자들을 더한 값을 반환
         */
        String query = "select concat('a','b', 'ㅇ') from Member m";
        List<String> resultList = em.createQuery(query).getResultList();
        String objects = resultList.get(0);
        System.out.println(objects);

        /**
         * 2. substring('abcd', 1,2) -> return 'ab'
         * @return A의 문자에 대하여, b와 c 까지의 문자열만 반환
         */
        query = "select substring('abcdefd', 1, 2) from Member m";
        resultList = em.createQuery(query).getResultList();
        objects = resultList.get(0);
        System.out.println(objects);

        /**
         * 3. length('abcd') -> 4
         * @return A의 문자에 대한 길이를 Integer로 반환
         */
        query = "select LENGTH('abcdefd') from Member m";
        List<Integer> integers = em.createQuery(query).getResultList();
        Integer integer = integers.get(0);
        System.out.println(integer);

        /**
         * 4. locate('b에 대한 검색할 문자','abcdㄹㄷ') ->  2
         * @return A의 문자에 대하여, b에 검색할 문자를 지정하고,
         *         검색 문자의 시작하는 위치값을 Integer로 반환
         */
        query = "select locate('b', 'abcdefd') from Member m";
        integers = em.createQuery(query).getResultList();
        integer = integers.get(0);
        System.out.println(integer);

        /**
         * 5. size(a,member)
         * @return 연관관계를 맺은 List 타입의 size를 Integer 반환
         * @Warning OneToMany에서 List<?>를 가진 Type에서만 조회 가능
         */
        Team team = Team.builder().name("ddd").build();
        teamRepository.save(team);
        query = "select size(t.members) from Team t";
        integers = em.createQuery(query).getResultList();
        integer = integers.get(0);
        System.out.println(integer); // 0반환

    }


    @Test
    void selectCollectionValueGraph() {


        Team team = Team.builder().name("ddd").build();
        teamRepository.save(team);

        Member member = new Member();
        member.setName("dd");
        member.entryTeam(team);
        memberRepository.save(member);

        Collection resultList1 = em.createQuery("select t.members from Team t").getResultList();
        resultList1.stream().forEach(result -> {
            Member member1 = (Member) result;
            System.out.println("member : " + member1.getName());
        });
    }

    @Test
//    @Transactional
    void manyToOneJoinFetchTest() {

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);

        Member member1 = new Member();
        member1.setName("member1");
        member1.entryTeam(team1);

        Member member2 = new Member();
        member2.setName("member2");
        member2.entryTeam(team1);

        memberRepository.saveAll(Arrays.asList(member1, member2));

        Team team2 = Team.builder().name("team2").build();
        teamRepository.save(team2);

        Member member3 = new Member();
        member3.setName("member3");
        member3.entryTeam(team2);
        memberRepository.save(member3);

        List<Member> all = memberRepository.findAll();

        for (Member member : all) {
            System.out.println("member3 = " + member.getName() + ", team name = " + member.getTeam().getName());
        }
        // N + 1, Member Query 1회, Team Query x Member count


    }

}
