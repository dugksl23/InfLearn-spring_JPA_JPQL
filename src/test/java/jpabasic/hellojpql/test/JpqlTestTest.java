package jpabasic.hellojpql.test;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.dto.MemberDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Test
    @DisplayName("insertTeam 테스트")
    @Rollback(value = false)
    @Transactional
    public void queryAnnotationPersistTest() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);

        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);

        // - Entity Projection.
        Member one = memberRepository.findOne(member1.getId());
        //@Query annotation은 영속성 컨텍스트를 통해서 flush가 아니라, jpql로 바로 db에 flush 하는 것이다.
        // 그러나 flush 이후, DB와 영속성 컨텍스트 간의 동기화를 하여,
        // 변경된 값에 대하여, 영속성 컨텍스트의 스냅샷과 비교하여 바뀐 부분에 대하여 다시금 1차 캐싱을 진행한다.
        // 즉, JPQL 또한 영속성 컨텍스트에 의해서 관리되고, 반환된다는 것이다.
        one.setAge(20);

    }

    @Test
    @DisplayName("findTeamFromMember 테스트")
    @Rollback(value = false)
    public void findTeamFromMemberTest() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);

        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);

        Team memberTeam = memberRepository.findMemberTeam(member.getId());

        log.info("team : {}", memberTeam.getName());

    }

    @Test
    @DisplayName("findTeamWithMemberJoin 테스트")
    @Rollback(value = false)
    public void findTeamWithMemberJoinTest() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);

        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);

        Team memberTeam = memberRepository.findMemberJoinWithTeam(member1.getId());

        log.info("team : {}", memberTeam.getName());

    }


    @Test
    @DisplayName("findMemberWithScala 테스트")
    @Rollback(value = false)
    public void findTeamWithScalaTest() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);

        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);

        // List<Object>로 받기
        List<Object[]> listObjArr = memberRepository.findMemberWithScalaReturnObjectArrList(member1.getId());
        Object[] objects = listObjArr.get(0);
        for (int i = 0; i < objects.length; i++) {
            log.info("value : {}", objects[i]);
        }

        Object objArr = memberRepository.findMemberWithScalaReturnObject(member1.getId());
        Object[] objects1 = (Object[]) objArr;

        log.info("size : {}", objects1.length);
        for (int i = 0; i < objects1.length; i++) {
            log.info("value : {}", objects1[i]);
        }

    }

    @Test
    @DisplayName("findMemberWithNewDto 테스트")
    @Rollback(value = false)
    public void findTeamWithNewDto() {

        Team team1 = Team.builder().name("team1").build();
        Team save = teamRepository.save(team1);

        Member member = Member.builder().name("member").age(10).build();
        member.entryTeam(save);

        Member member1 = memberRepository.save(member);
        MemberDto memberWithNewDto = memberRepository.findMemberWithNewDto(member1.getId());


    }

}