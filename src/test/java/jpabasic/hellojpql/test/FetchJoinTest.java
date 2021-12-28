package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
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
import java.util.stream.Collectors;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FetchJoinTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    //    @BeforeEach
    @Rollback(value = false)
    void manyToOneJoinTest() {

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
    void findTeamWithJoin() {

        List<Team> all = em.createQuery("select t from Team t join t.members").getResultList();
        System.out.println("all size : " + all.size());
        for (int i = 0; i < all.size(); i++) {
            System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().size());
            for (int j = 0; j < all.get(i).getMembers().size(); j++) {
                System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().get(j).getName());
            }
        }
    }

    @Test
    @Rollback(value = false)
    @Transactional
    void findTeamWithJoinFetch() {

        List<Team> all = em.createQuery("select t from Team t join fetch t.members").getResultList();
        System.out.println("all size : " + all.size());
        for (int i = 0; i < all.size(); i++) {
            System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().size());
            for (int j = 0; j < all.get(i).getMembers().size(); j++) {
                System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().get(j).getName());
            }
        }

    }

    @Test
    @Rollback(value = false)
    @Transactional
    void findMemberWithJoinFetchUsingDistinct() {

        List<Team> all = em.createQuery("select distinct t from Team t join fetch t.members").getResultList();
        System.out.println("all size : " + all.size());
        for (int i = 0; i < all.size(); i++) {
            System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().size());
            for (int j = 0; j < all.get(i).getMembers().size(); j++) {
                System.out.println("team name = " + all.get(i).getName() + ", member size :   = " + all.get(i).getMembers().get(j).getName());
            }
        }

        List<Team> collect = teamRepository.findAll().stream().distinct().collect(Collectors.toList());
        System.out.println("all size : " + collect.size());
        for (int i = 0; i < collect.size(); i++) {
            System.out.println("team name = " + collect.get(i).getName() + ", member size :   = " + collect.get(i).getMembers().size());
            for (int j = 0; j < collect.get(i).getMembers().size(); j++) {
                System.out.println("team name = " + collect.get(i).getName() + ", member size :   = " + collect.get(i).getMembers().get(j).getName());
            }
        }
    }

}
