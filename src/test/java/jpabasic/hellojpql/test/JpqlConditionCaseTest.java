package jpabasic.hellojpql.test;


import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.MemberType;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class JpqlConditionCaseTest {

    @Autowired
    private MemberRepository memberRepository;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private TeamRepository teamRepository;


    @BeforeEach
    void insertDummy() {

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setName("member" + i);
            member.entryTeam(team1);
            memberRepository.save(member);
        }
    }


    @Test
    void basicCaseTest() {

        String query =
                "select " +
                        "case " +
                        " when m.id >= 5 then '회원'" +
                        " when m.id <= 6 then '비회원'" +
                        " else '무료'" +
                        " end " +
                        "from Member m";

        List<String> resultList = em.createQuery(query).getResultList();
        for (String string : resultList) {
            System.out.println("string = " + string);
        }
    }

    @Test
    void simpleCaseTest() {

        String query =
                "select " +
                        "case m.id" +
                        " when  5 then '회원'" +
                        " when  6 then '비회원'" +
                        " else '무료'" +
                        " end " +
                        "from Member m";

        List<String> resultList = em.createQuery(query).getResultList();
        for (String string : resultList) {
            System.out.println("string = " + string);
        }
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

}
