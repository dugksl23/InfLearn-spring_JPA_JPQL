package jpabasic.hellojpql.repository;

import jpabasic.hellojpql.domain.Member;
import jpabasic.hellojpql.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
