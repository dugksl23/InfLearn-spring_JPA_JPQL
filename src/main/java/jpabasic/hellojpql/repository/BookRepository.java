package jpabasic.hellojpql.repository;


import jpabasic.hellojpql.domain.Book;
import jpabasic.hellojpql.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
