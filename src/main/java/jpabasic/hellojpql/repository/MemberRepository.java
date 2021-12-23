package jpabasic.hellojpql.repository;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import jpabasic.hellojpql.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 0. 엔티티로 조회
     *
     * @Return Member
     * Alias는 반드시 넣어야 한다. 다만 as는 생략 가능
     */
    @Query("select m from Member m where m.id = ?1")
    public Member findOne(@Param("id") Long id);

    /**
     * 0. 엔티티로 조회 및 정렬 함수
     *
     * @Return Integer
     * sum(), max(), min(), avg()
     */
    @Query("select sum(m.age) from Member m")
    public Integer findOneSort();


    /**
     * 1. 묵시적 join 문법(간접)
     *
     * @Return Team
     * m.team 의 select 는 곧 join 하여 값을 불러오겠다는 것이다.
     * 이 경우는 성능상 문제가 있으며, 직관적이지 않다.
     */
    @Query("select m.team from Member m where m.id = :id")
    public Team findMemberTeam(@Param("id") Long id);


    /**
     * 2. 명시적 join 문법(직접)
     *
     * @Return Team
     * 조인이 필요하다면, 직관적으로 join 과 함께 사용해야 한다.
     */
    @Query("select t from Member m join m.team t where m.id = :id")
    public Team findMemberJoinWithTeam(@Param("id") Long id);

    /**
     * 3. 스칼라 조인 (일반 sql 과 유사한 기본 데이터 나열)
     *
     * @Return List<Object [ ]> list
     * List<Object> 로 받으면, return 값은 [1,2] 로 출력
     * Object[] 또한 0번 인덱스의 [1,2] 로만 출력
     */
    @Query("select m.id, m.age from Member m where m.id = :id")
    public List<Object[]> findMemberWithScalaReturnObjectArrList(@Param("id") Long id);

    @Query("select m.id, m.age from Member m where m.id = :id")
    public Object findMemberWithScalaReturnObject(@Param("id") Long id);

    /**
     * 4. Dto 를 통한 new 명령 조회
     * @Return MemberDto
     * 해당 Dto의 패키지 full 경로 입력해야 한다.
     */
    @Query(value = "select new jpabasic.hellojpql.dto.MemberDto(m.id, m.age) from Member as m where m.id = :id")
    public MemberDto findMemberWithNewDto(@Param("id") Long id);

}

