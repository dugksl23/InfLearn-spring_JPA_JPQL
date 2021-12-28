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
        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setAge(i);
            member.setName("member" + i);
            member.entryTeam(team1);
            memberRepository.save(member);
        }
    }

    @Test
    @Rollback(value = false)
    @Transactional
    void pagingTest() {

        String query = "select m from Member m order by m.id desc";
        List<Member> resultList = em.createQuery(query, Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        resultList.forEach(result -> {
            System.out.println("member : " + result.getId() + ", team : " + result.getTeam().getName());
        });


    }
}
