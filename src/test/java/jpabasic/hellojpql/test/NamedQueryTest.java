package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class NamedQueryTest {

    private MemberRepository memberRepository;
    private TeamRepository teamRepository;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    void init(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    @BeforeEach
    void insertDummy() {

        Team team1 = Team.builder().name("team1").build();
        teamRepository.save(team1);

        Member member = new Member();
        member.setName("member1");
        member.entryTeam(team1);

        Member member2 = new Member();
        member.setName("member2");
        member.entryTeam(team1);

        memberRepository.saveAll(Arrays.asList(member, member2));

    }

    @Test
    @Transactional
    void namedQueryTest() {

        Optional<Team> byId = teamRepository.findById(1l);
        if (byId.isPresent()) {
            List<Member> team = em.createNamedQuery("Member.findByTeam", Member.class)
                    .setParameter("team", byId.get())
                    .getResultList();
            for (Member member : team) {
                log.info("member = {}, team = {}", member, member.getTeam().getName());
            }
        }
    }

}
