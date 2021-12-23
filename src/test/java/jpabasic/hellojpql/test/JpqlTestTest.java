package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.repository.MemberRepository;
import jpabasic.hellojpql.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class JpqlTestTest {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    @DisplayName("insertTeam 테스트")
    @Rollback(value = false)
    public void insertMemberWithTeam() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);


        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);
        Integer oneSort = memberRepository.findOneSort();
        log.info("age : {}", oneSort);

    }


}