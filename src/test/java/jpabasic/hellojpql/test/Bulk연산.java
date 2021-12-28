package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.MemberType;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StopWatch;

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

    //    @BeforeEach
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
    @Rollback(value = false)
    @Transactional
    void bulkInsert() {

//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
        List<Member> members = new ArrayList<>();
        //3_000_000
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
                });


//        memberRepository.saveAll(members);
//        members.forEach(member -> em.persist(member));
//        em.flush();
//        em.clear();

    }


}
