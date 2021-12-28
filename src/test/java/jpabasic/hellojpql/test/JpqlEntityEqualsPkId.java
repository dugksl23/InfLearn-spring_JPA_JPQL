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
public class JpqlEntityEqualsPkId {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @PersistenceContext
    private EntityManager em;

//    @Autowired
//    void init(MemberRepository memberRepository, TeamRepository teamRepository) {
//        this.memberRepository = memberRepository;
//        this.teamRepository = teamRepository;
//    }


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


      /**
     * 3. Entity 를 파라미터로 전달.
     */
    @Test
    @Transactional
    void fkEntityType() {

        String sql = "select m from Member m where m.team = :team";
        Optional<Team> byId = teamRepository.findById(1L);
        if (byId.isPresent()) {
            List<Member> resultList = em.createQuery(sql, Member.class).setParameter("team", byId.get()).getResultList();
            for (Member member : resultList) {
                log.info("member = {}, team = {}", member, member.getTeam().getName());
            }
        }
    }

    /**
     * 4. Entity 의 FK 를 파라미터로 전달.
     */
    @Test
    @Transactional
    void fkEntityPKType() {

        String sql = "select m from Member m where m.team.id = :teamId";
        Optional<Team> byId = teamRepository.findById(1L);
        if (byId.isPresent()) {
            List<Member> resultList = em.createQuery(sql, Member.class).setParameter("teamId", byId.get().getId()).getResultList();
            for (Member member : resultList) {
                log.info("member = {}, team = {}", member, member.getTeam().getName());
            }
        }
    }

    /**
     * 1. Entity 를 파라미터로 전달.
     */
    @Test
    @Transactional
    void pkEntityType() {

        String sql = "select m from Member m where m = :member";
        Optional<Member> byId = memberRepository.findById(1L);
        if (byId.isPresent()) {
            List<Member> resultList = em.createQuery(sql, Member.class).setParameter("member", byId.get()).getResultList();
            for (Member member : resultList) {
                log.info("member = {}, team = {}", member, member.getTeam().getName());
            }
        }
    }



    /**
     * 2. Entity 의 PK 를 파라미터로 전달.
     */
    @Test
    @Transactional
    void pkEntityPKType() {

        String sql = "select m from Member m where m.id = :memberId";
        Optional<Member> byId = memberRepository.findById(1L);
        if (byId.isPresent()) {
            List<Member> resultList = em.createQuery(sql, Member.class).setParameter("memberId", byId.get().getId()).getResultList();
            for (Member member : resultList) {
                log.info("member = {}, team = {}", member, member.getTeam().getName());
            }
        }
    }

}
