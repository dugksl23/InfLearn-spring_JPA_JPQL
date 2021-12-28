package jpabasic.hellojpql.test;


import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JpaPagingTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;


//    @BeforeEach
    void insertDummy() {
        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);

        Member member1 = new Member();
        member1.setName("member1");
        member1.entryTeam(team1);

        Member member2 = new Member();
        member2.setName("member2");
        member2.entryTeam(team1);

        List<Member> members = memberRepository.saveAll(Arrays.asList(member1, member2));

        Team team2 = Team.builder().name("team2").build();
        teamRepository.save(team2);

        Member member3 = new Member();
        member3.setName("member3");
        member3.entryTeam(team2);
        memberRepository.save(member3);
    }

    @Test
    @Rollback(value = false)
    @Transactional
    void pagingTest() {

        String query = "select m from Member m join fetch m.team order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .setFirstResult(0)
                .setMaxResults(0)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId() + ", team : " + result.getTeam().getName());
        });
    }

    @Test
    @Rollback(value = false)
    @Transactional
    void paging일대다Test() {

        String query = "select t from Team t";
        List<Team> all = em.createQuery(query)
                .setFirstResult(0)
                .setMaxResults(2)
                .getResultList();

        for (int i = 0; i < all.size(); i++) {
            System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().size());
            for (int j = 0; j < all.get(i).getMembers().size(); j++) {
                System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().get(j).getName());
            }
        }
    }
}
