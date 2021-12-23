package jpabasic.hellojpql.repository;

import jpabasic.hellojpql.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    @Query("select m from Member m where m.id = ?1")
    public Member findOne(@Param("id") Long id);

    @Query("select sum(m.age) from Member m")
    public Integer findOneSort();

}
