package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.MemberType;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class Bulk연산 {

    private MemberRepository memberRepository;
    private TeamRepository teamRepository;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        member.setAge(20);
        member.entryTeam(team1);

        Member member2 = new Member();
        member2.setName("member2");
        member2.setAge(20);
        member2.entryTeam(team1);

        memberRepository.saveAll(Arrays.asList(member, member2));

    }

    @Test
    @Transactional
    void bulkUpdate() {

        String query = "update Member m set m.age = :age";
        int age = em.createQuery(query)
                .setParameter("age", 20)
                .executeUpdate();

        System.out.println("updated age : " + age);
        // java.lang.IllegalArgumentException: Update/delete queries cannot be typed
        // 타입 지정못함.
    }

    @Test
    @Transactional
    void bulkDelete() {

        String query = "delete from Member m where m.age = :age";
        List<Member> resultList = em.createQuery("select m from Member m where m.age = :age").setParameter("age", 20).getResultList();
        System.out.println("resultList = " + resultList.size());

        int age = em.createQuery(query)
                .setParameter("age", 20)
                .executeUpdate();

        System.out.println("delete age : " + age);

    }


    @Test
    @Rollback(value = false)
    @Transactional
    void bulkInsert() {

        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3_000_000; i++) {
            Member member = new Member();
            member.setName("member" + i);
            member.setAge(i);
            members.add(member);
        }

        jdbcTemplate.batchUpdate("insert into Member values (NEXTVAL('Member_Seq'),?,?,?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        ps.setInt(1, members.get(i).getAge());
                        ps.setString(2, MemberType.ADMIN.name());
                        ps.setString(3, members.get(i).getName());
                        ps.setString(4, null);

                    }

                    @Override
                    public int getBatchSize() {
                        return members.size();
                    }
                }
        );

    }

    @Test
    @Rollback(value = false)
    @Transactional
    void jdbcTemplateAndPersistence() {

        int i = em.createQuery("update Member m set m.age = :age where m.id = :id")
                .setParameter("age", 30)
                .setParameter("id", 1l)
                .executeUpdate();
        System.out.println("size = " + i);

        // 영속성 컨텍스트와 DB 동기화를 위해, 영컨 초기화
        em.clear();

        Member member = em.find(Member.class, 1l);
        System.out.println("member = " + member.toString());

    }

}
